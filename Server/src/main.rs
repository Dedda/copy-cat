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
use serde::de::StdError;

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

struct VersionError {
    pub expected: i32,
    pub actual: i32,
}

fn check_protocol_version(version: &i32) -> Result<(), VersionError> {
    if version.ne(&PROTOCOL_VERSION) {
        return Err(VersionError {
            expected: PROTOCOL_VERSION,
            actual: version.clone(),
        });
    }
    return Ok(());
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

impl From<VersionError> for ClipboardPushResponse {
    fn from(error: VersionError) -> Self {
        ClipboardPushResponse::make_error(format!("Unsupported version {}. Expected {}", error.actual, error.expected))
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

impl From<Box<dyn StdError>> for ClipboardPushResponse {
    fn from(error: Box<dyn StdError>) -> Self {
        ClipboardPushResponse::make_error(format!("General error: {}", error))
    }
}

#[post("/push", format = "application/json", data = "<push>")]
async fn push(push: Json<ClipboardPush>) -> Json<ClipboardPushResponse> {
    if let Err(err) = check_protocol_version(&push.version) {
        let response: ClipboardPushResponse = err.into();
        error!("{}", response.error.clone().unwrap());
        return Json(response);
    }
    let response = match push.clipboard_type.as_str() {
        "text" => {
            match handle_push_text(&push) {
                Err(err) => err,
                _ => ClipboardPushResponse::make_ok(),
            }
        }
        _ => {
            let error_msg = "Unknown clipboard_type";
            error!("{}", error_msg);
            ClipboardPushResponse::make_error(error_msg.into())
        }
    };
    Json(response)
}

fn handle_push_text(push: &Json<ClipboardPush>) -> Result<(), ClipboardPushResponse> {
    let text = parse_push_text(push)?;
    set_clipboard_text(text)?;
    Ok(())
}

fn parse_push_text(push: &Json<ClipboardPush>) -> Result<String, ClipboardPushResponse> {
    let raw = base64::decode(push.contents.clone())?;
    let text= String::from_utf8(raw)?;
    return Ok(text);
}

fn set_clipboard_text(text: String) -> Result<(), ClipboardPushResponse> {
    let mut ctx: ClipboardContext = ClipboardProvider::new()?;
    Ok(ctx.set_contents(text)?)
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

    fn make_error(content_type: Option<String>, msg: String) -> Self {
        Self {
            version: PROTOCOL_VERSION,
            clipboard_type: content_type.unwrap_or(String::new()),
            contents: None,
            error: Some(msg),
        }
    }

    fn make_unsupported_clipboard_type_error(clipboard_type: String) -> Self {
        Self {
            version: PROTOCOL_VERSION,
            error: Some(format!("Unsupported content_type: {}", clipboard_type)),
            clipboard_type: clipboard_type,
            contents: None,
        }
    }
}

impl From<VersionError> for ClipboardPullResponse {
    fn from(error: VersionError) -> Self {
        ClipboardPullResponse::make_error(None, format!("Unsupported version {}. Expected {}", error.actual, error.expected))
    }
}

impl From<Box<dyn StdError>> for ClipboardPullResponse {
    fn from(error: Box<dyn StdError>) -> Self {
        ClipboardPullResponse::make_error(None, format!("General error: {}", error))
    }
}

#[post("/request", format = "application/json", data = "<request>")]
fn request(request: Json<ClipboardRequest>) -> Json<ClipboardPullResponse> {
    if let Err(err) = check_protocol_version(&request.version) {
        let response: ClipboardPullResponse = err.into();
        error!("{}", response.error.clone().unwrap());
        return Json(response);
    }
    let response = match request.clipboard_type.as_str() {
        "text" => {
            match handle_pull_text() {
                Ok(text) => ClipboardPullResponse::make_text(text),
                Err(err) => err,
            }
        }
        unsupported => {
            error!("Unknown clipboard_type `{}`", unsupported);
            ClipboardPullResponse::make_unsupported_clipboard_type_error(unsupported.into())
        }
    };
    Json(response)
}

fn handle_pull_text() -> Result<String, ClipboardPullResponse> {
    let mut ctx: ClipboardContext = ClipboardProvider::new()?;
    let contents = ctx.get_contents()?;
    let contents = base64::encode(contents);
    return Ok(contents)
}
