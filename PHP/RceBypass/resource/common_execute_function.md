### system()

```
system(string $command, int &$return_var = ?): string
```

执行系统命令，**有回显**

### passthru()

```
passthru(string $command, int &$return_var = ?): void
```

执行系统命令并且**显示原始输出**

### shell_exec()

```
shell_exec(string $cmd): string)
```

通过shell环境执行命令，并且将完整的输出以字符串形式返回(**无回显**)

### exec()

```
exec(string $command, array &$output = ?, int &$return_var = ?): string
```

执行一个外部程序， 同时**无回显**，且输出的时候仅返回命令的最后一行

### 反引号

```text
echo `ls`;
```

只要在反引号里的字符串都会被当作代码执行，**注意**如果反引号在单、双引号内则不起作用

### 回调函数

这类型函数会在代码执行的时候讲到，故这里只说明一点，如果回调函数执行的是上述的执行系统命令的函数，那么回调函数也可以被当成命令执行使用