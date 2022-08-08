### 命令执行

```php
$black_list=array('^','.','`','>','<','=','"','preg','&','|','%0','popen','char','decode','html','md5','{','}','post','get','file','ascii','eval','replace','assert','exec','$','include','var','pastre','print','tail','sed','pcre','flag','scan','decode','system','func','diff','ini_','passthru','pcntl','proc_open','+','cat','tac','more','sort','log','current','\\','cut','bash','nl','wget','vi','grep', '~');
$aaa = str_ireplace($black_list,"hacker",$aaa);
```

disabled_function:

```php
system,exec,shell_exec,passthru,proc_open,proc_close, proc_get_status,checkdnsrr,getmxrr,getservbyname,getservbyport, syslog,popen,show_source,highlight_file,dl,socket_listen,socket_create,socket_bind,socket_accept, socket_connect, stream_socket_server, stream_socket_accept,stream_socket_client,ftp_connect, ftp_login,ftp_pasv,ftp_get,sys_getloadavg,disk_total_space, disk_free_space,posix_ctermid,posix_get_last_error,posix_getcwd, posix_getegid,posix_geteuid,posix_getgid, posix_getgrgid,posix_getgrnam,posix_getgroups,posix_getlogin,posix_getpgid,posix_getpgrp,posix_getpid, posix_getppid,posix_getpwnam,posix_getpwuid, posix_getrlimit, posix_getsid,posix_getuid,posix_isatty, posix_kill,posix_mkfifo,posix_setegid,posix_seteuid,posix_setgid, posix_setpgid,posix_setsid,posix_setuid,posix_strerror,posix_times,posix_ttyname,posix_uname,scandir,readfile
```

