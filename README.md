# CopyCat

CopyCat is going to be a simple solution to easily share clipboard contents between different devices. 
As a first step, only mobile to desktop and vice versa will be supported.

## How does it work?

### Desktop

There will be a cross platform server written in Rust, that provides access to the clipboard.

### Mobile

Upon starting, the desktop server will print a QR code that can be scanned on the phone to connect with.
From then on, sharing or querying your current clipboard contents is easily be done from the start screen of the app.
Phone apps can be connected to multiple servers at the same time.

The mobile app will be written in Kotlin Multiplatform Mobile.
