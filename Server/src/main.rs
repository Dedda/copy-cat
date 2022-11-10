extern crate core;
#[macro_use]
extern crate rocket;

use std::string::FromUtf8Error;

use base64::DecodeError;
use clipboard::{ClipboardContext, ClipboardProvider};
use local_ip_address::local_ip;
use rocket::{Build, post, Rocket};
use rocket::serde::{Deserialize, Serialize};
use rocket::serde::json::Json;

const PROTOCOL_VERSION: i32 = 1;

#[launch]
fn rocket() -> Rocket<Build> {
    let link = get_local_link();
    println!("{}", link);
    qr2term::print_qr(&link).unwrap_or_else(|_| {
        eprintln!("Could not print link QR-Code")
    });
    rocket::build().mount("/", routes![push, request])
}

fn get_local_link() -> String {
    let ip_addr = local_ip().expect("Cannot get local IP address");
    let port = 8000;
    format!("copycat://connect.app?address={}:{}", ip_addr, port)
}

#[derive(Deserialize)]
#[serde(crate = "rocket::serde")]
struct ClipboardPush {
    pub version: i32,
    pub clipboard_type: String,
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

impl From<DecodeError> for ClipboardPushResponse {
    fn from(error: DecodeError) -> Self {
        ClipboardPushResponse::make_error(format!("Unable to parse text content: {}", error))
    }
}

impl From<FromUtf8Error> for ClipboardPushResponse {
    fn from(error: FromUtf8Error) -> Self {
        ClipboardPushResponse::make_error(format!("Unable to parse text content: {}", error))
    }
}

#[post("/push", format = "application/json", data = "<push>")]
async fn push(push: Json<ClipboardPush>) -> Json<ClipboardPushResponse> {
    if let Some(err) = check_version(&push.version) {
        return Json(ClipboardPushResponse::make_error(err));
    }
    let response = match push.clipboard_type.as_str() {
        "text" => {
            if let Some(text) = parse_push_text(&push) {
                debug!("Received text clipboard `{}` from client.", text);
                if let Err(msg) = set_clipboard_text(text) {
                    ClipboardPushResponse::make_error(msg)
                } else {
                    ClipboardPushResponse::make_ok()
                }
            } else {
                ClipboardPushResponse::make_error("Unable to parse text content".into())
            }
        }
        _ => {
            let error_msg = "Unknown clipboard_type";
            error!("{}", error_msg);
            ClipboardPushResponse::make_error(error_msg.into())
        }
    };
    return Json(response);
}

fn parse_push_text(push: &Json<ClipboardPush>) -> Option<String> {
    if let Ok(raw) = base64::decode(push.contents.clone()) {
        if let Ok(text) = String::from_utf8(raw) {
            return Some(text);
        }
    }
    return None;
}

fn set_clipboard_text(text: String) -> Result<(), String> {
    if let Ok(ctx) = ClipboardProvider::new() {
        let mut ctx: ClipboardContext = ctx;
        return ctx.set_contents(text).map_err(|_| "Unable to set clipboard text".into());
    }
    return Err("Unable to get clipboard context".into())
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
    if let Some(err) = check_version(&request.version) {
        return Json(ClipboardPullResponse::make_error(request.clipboard_type.clone(), err));
    }
    return match request.clipboard_type.as_str() {
        "text" => {
            let mut ctx: ClipboardContext = ClipboardProvider::new().unwrap();
            let contents = ctx.get_contents().unwrap();
            debug!("Sending contents `{}` to client", contents);
            Json(ClipboardPullResponse::make_text(base64::encode(contents)))
        }
        unsupported => {
            let error_msg = format!("Unknown clipboard_type `{}`", unsupported);
            error!("{}", error_msg);
            Json(ClipboardPullResponse::make_error(request.clipboard_type.clone(), error_msg.into()))
        }
    };
}

fn check_version(version: &i32) -> Option<String> {
    if version.ne(&PROTOCOL_VERSION) {
        let error_msg = format!("Unsupported version {}. Expected {}", version, PROTOCOL_VERSION);
        error!("{}", error_msg);
        return Some(error_msg);
    }
    return None;
}