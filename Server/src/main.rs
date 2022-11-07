use local_ip_address::local_ip;
// use urlencoding::encode;

fn main() {
    let ip_addr = local_ip().unwrap();
    let link = format!("copycat://connect.app?address={}", ip_addr);
    println!("{}", link);
    qr2term::print_qr(&link).unwrap();
}
