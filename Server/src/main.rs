extern crate core;
#[macro_use]
extern crate rocket;

use clipboard::{ClipboardContext, ClipboardProvider};
use local_ip_address::local_ip;
use rocket::{Build, post, Rocket};
use rocket::serde::{Deserialize, Serialize};
use rocket::serde::json::Json;

const PROTOCOL_VERSION: i32 = 1;

#[launch]
fn rocket() -> Rocket<Build> {
    let ip_addr = local_ip().unwrap();
    let port = 8000;
    let link = format!("copycat://connect.app?address={}:{}", ip_addr, port);
    println!("{}", link);
    qr2term::print_qr(&link).unwrap();
    rocket::build().mount("/", routes![push, request])
}

#[derive(Deserialize)]
#[serde(crate = "rocket::serde")]
struct ClipboardPush {
    pub version: i32,
    pub content_type: String,
    pub contents: String,
}

#[derive(Serialize)]
#[serde(crate = "rocket::serde")]
struct ClipboardPushResponse {
    pub version: i32,
    pub error: Option<String>,
}

impl ClipboardPushResponse {
    fn make_ok() -> Self {
        Self {
            version: PROTOCOL_VERSION,
            error: None,
        }
    }

    fn make_error(msg: String) -> Self {
        Self {
            version: PROTOCOL_VERSION,
            error: Some(msg),
        }
    }
}

#[post("/push", format = "application/json", data = "<push>")]
async fn push(push: Json<ClipboardPush>) -> Json<ClipboardPushResponse>{
    if push.version.ne(&PROTOCOL_VERSION){
        return Json(ClipboardPushResponse::make_error(format!("Unsupported version {}. Expected {}", push.version, PROTOCOL_VERSION)))
    }
    let response = match push.content_type.as_str() {
        "text" => {
            let mut ctx: ClipboardContext = ClipboardProvider::new().unwrap();
            let raw = base64::decode(push.contents.clone()).unwrap();
            ctx.set_contents(String::from_utf8(raw).unwrap()).unwrap();
            ClipboardPushResponse::make_ok()
        },
        _ => ClipboardPushResponse::make_error("Unknown clipboard_type".into()),
    };
    return Json(response)
}

#[derive(Deserialize)]
#[serde(crate = "rocket::serde")]
struct ClipboardRequest {
    pub version: i32,
    pub clipboard_type: String,
}

#[derive(Serialize)]
#[serde(crate = "rocket::serde")]
struct ClipboardPullResponse {
    pub version: i32,
    pub clipboard_type: String,
    pub contents: Option<String>,
    pub error: Option<String>,
}

impl ClipboardPullResponse {
    fn make_text(contents: String) -> Self {
        Self {
            version: PROTOCOL_VERSION,
            clipboard_type: "text".into(),
            contents: Some(contents),
            error: None,
        }
    }

    fn make_error(content_type: String, msg: String) -> Self {
        Self {
            version: PROTOCOL_VERSION,
            clipboard_type: content_type,
            contents: None,
            error: Some(msg),
        }
    }
}

#[post("/request", format = "application/json", data = "<request>")]
fn request(request: Json<ClipboardRequest>) -> Json<ClipboardPullResponse> {
    if request.version.ne(&PROTOCOL_VERSION){
        return Json(ClipboardPullResponse::make_error(request.clipboard_type.clone(), format!("Unsupported version {}. Expected {}", request.version, PROTOCOL_VERSION)))
    }
    return match request.clipboard_type.as_str() {
        "text" => {
            let mut ctx: ClipboardContext = ClipboardProvider::new().unwrap();
            let contents = ctx.get_contents().unwrap();
            Json(ClipboardPullResponse::make_text(base64::encode(contents)))
        },
        unsupported => Json(ClipboardPullResponse::make_error(unsupported.into(), "Unsupported clipboard_type".into()))
    }
}