def hex_payload(payload):
	res_payload = ''
	for i in payload:
		i = "\\x" + hex(ord(i))[2:]
		res_payload += i
	print("[+]'{}' Convert to hex: \"{}\"".format(payload,res_payload))

def oct_payload(payload):
	res_payload = ""
	for i in payload:
		i = "\\" + oct(ord(i))[2:]
		res_payload += i
	print("[+]'{}' Convert to oct: \"{}\"".format(payload,res_payload))

def uni_payload(payload):
	res_payload = ""
	for i in payload:
		i = "\\u{{{0}}}".format(hex(ord(i))[2:])
		res_payload += i
	print("[+]'{}' Convert to unicode: \"{}\"".format(payload,res_payload))

if __name__ == '__main__':
	payload = 'phpinfo'
	hex_payload(payload)
	oct_payload(payload)
	uni_payload(payload)