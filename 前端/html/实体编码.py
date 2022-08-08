from cgitb import text
import sys
import argparse

def translet(content, base):
    result = ""
    for i in content:
        if base == 'hex':
            result += "&#x" + hex(ord(i)).replace("0x", "") + ";"
        elif base == 'dec':
            result += "&#" + str(ord(i)) + ";"
        elif base == 'url':
            result += "%" + hex(ord(i)).replace("0x", "") + ";"
    return result
        

def parse_args():
    parser = argparse.ArgumentParser(description='这是用来进行html实体编码转化')
    # 帮助信息
    parser.add_argument('--content', '-c', metavar="payload", required=True, help="payload will be translet")
    parser.add_argument('--base', '-b', metavar="hex/dec/url",choices={'hex', 'dec', 'url'} , required=True, default="hex", help="which base you want to translet")
    args = parser.parse_args()
    if args.content:
        content = args.content
    if args.base:
        base = args.base
    return translet(content, base)

if __name__ == '__main__':
    result = parse_args()
    print(result)