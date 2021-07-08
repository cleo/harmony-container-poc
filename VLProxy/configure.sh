#!/usr/bin/expect -f

lassign $argv INSTALL_DIR SERIAL_NUMBERS
set HTTPS_PORT 6443
set SFTP_PORT 10022
set INTERNAL_ADDRESS 127.0.0.1
set EXTERNAL_ADDRESS 127.0.0.1

set timeout 20
debug 0
spawn $INSTALL_DIR/VLProxyc -p
expect "Enter password:"
send "Admin\r"
expect "Enter number of item you wish to change or 0 to exit:"
send "2\r"
expect "Enter serial numbers separated by commas:"
send "$SERIAL_NUMBERS\r"
expect "Enter number of item you wish to change or 0 to exit:"
send "8\r"
expect "Enter External Reverse Proxy HTTPs ports separated by commas:"
send "$HTTPS_PORT\r"
expect "Enter number of item you wish to change or 0 to exit:"
send "13\r"
expect "Enter External Reverse Proxy SSH FTP ports separated by commas:"
send "$SFTP_PORT\r"
expect "Enter number of item you wish to change or 0 to exit:"
send "27\r"
expect "Reverse Proxy Load Balancing (Yes/No):"
send "Yes\r"
expect "Enter number of item you wish to change or 0 to exit:"
send "4\r"
expect "Enter your internal network IP address for the Proxy Computer:"
send "$INTERNAL_ADDRESS\r"
expect "Enter number of item you wish to change or 0 to exit:"
send "6\r"
expect "Enter External Addresses for the Proxy Computer separated by commas:"
send "$EXTERNAL_ADDRESS\r"
expect "Enter number of item you wish to change or 0 to exit:"
send "0\r"

