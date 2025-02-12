# ping-monitor

# 拨测系统思路文字描述：

- 拨测机器人：采用SPI技术实现插件，每个拨测插件的唯一名字用java包名代替  
例如，下面是3个拨测插件唯一名字:  
```
plugs {
	com.xxx.sss.PingCallTest,
	com.xxx.sss.HttpCallTest,
	com.xxx.sss.XXXXCallTest
}
```
- 拨测系统为分布式应用，基于zookeeper协调，zk数据结构如下  
以下是基于一个zk命名空间下
```
/config (拨测配置) {
	拨测周期
}
/robot (拨测机器人路径) {
	/param-templates (拨测参数模版，注意 key是拨测插件名 value是模版描述) {
		com.xxx.sss.PingCallTest = vaule 是参数模版
		com.xxx.sss.HttpCallTest = vaule 是参数模版
		com.xxx.sss.XXXXCallTest = vaule 是参数模版
	}
	/register (机器人注册路径) {
		/rebot-xxx (机器人名字,里面多个实例都是一组的) {
			/ip@procid (机器人实例名字 __临时节点__，ip地址 + 进程ID 组合) {
			}
			/ip@procid (机器人实例名字 __临时节点__，ip地址 + 进程ID 组合) {
			}
			/strategy (拨测策略) {
				狂轰同时拨测,测试周期失效，各个实例全力创建多个线程拨测，可用于压测
				选主一个拨测,可用于需要高可用稳定拨测场景，会顺序拨测作业列表
			}
			/job-tasks (拨测任务列表) {
				/task-01
				/task-02
			}
		}
	}
}

/tasks (拨测任务配置) {
	/task-01  (任务名称) {
		plugin = com.xxx.sss.PingCallTest
		config = { "ip": "127.0.0.1", "port": 3251 }
		ping-result(拨测结果) {
			/rebot-xxx {300ms}
		}
	}
	/task-02  (任务名称) {
		plugin = com.xxx.sss.HttpCallTest
		config = { "url": "https://baidu.com" }
		ping-result(拨测结果) {
			/rebot-xxx {300ms}
		}
	}
}

```

- 代码模块设计，如下结构  
```
ping-monitor
│ 
│    
├── ping-common         // 公用部分代码
│   ├── src
│   └── pom.xml
│ 
├── ping-manager        // 拨测后台服务，内置zookeeper便于开发测试
│   ├── src
│   └── pom.xml
│ 
├── ping-manager-web    // 拨测后台服务，API网关
│   ├── src
│   └── pom.xml
│ 
├── ping-manager-ui     // 拨测后台服务，内置zookeeper便于开发测试
│   ├── src
│   └── package.json
│ 
├── ping-metric-exporter// 拨测结果监控感知，导出到Pro米修斯
│   ├── src
│   └── pom.xml
│
└── ping-robot          // 拨测机器人软件开发包
    │ 
    ├── ping-robot-api  // 支持机器人组开发件
    │   ├── src 
    │   └── pom.xml
    │ 
    ├── ping-robot-man  // 拨测机器人实现
    │   ├── src 
    │   └── pom.xml
    │ 
    └── pom.xml
```

- 模块伪代码，拨测参数模版 /robot/param-templates 下面  
```
public class TemplateField {
    private String key;       // 属性的 key
    private String type;      // 属性的类型（如 String, Integer 等）

    // Constructor, Getters and Setters
    public TemplateField(String key, String type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}


public class Template {
    private List<TemplateField> fields;

    // Constructor, Getters and Setters
    public Template(List<TemplateField> fields) {
        this.fields = fields;
    }

    public List<TemplateField> getFields() {
        return fields;
    }

    public void setFields(List<TemplateField> fields) {
        this.fields = fields;
    }
}
```
https://chatgpt.com/share/6768b8f7-35f8-8003-89b0-10e4a36f09e2


流水线

      - name: Install Prettier globally
        run: npm install -g prettier

      - name: Apply formatting tools
        run: |
          prettier --write "**/*.{js,jsx,ts,tsx,json,css,scss,html,md}"
          find . -name "*.java" | xargs java -jar google-java-format.jar --replace


public class ZookeeperDataConver {

    @FunctionalInterface
    public static interface IConvertToByte<T> {
        byte[] getData(T param);
    }

    @FunctionalInterface
    public static interface IConvertFromByte<T> {
        T getValue(byte[] param);
    }

    // 返回转换为 byte[] 的 IConvertToByte
    public IConvertToByte<Integer> getConvertToByteForInteger() {
        return param -> Integer.toString(param).getBytes();
    }

    // 返回从 byte[] 转换为 Integer 的 IConvertFromByte
    public IConvertFromByte<Integer> getConvertFromByteForInteger() {
        return param -> Integer.valueOf(new String(param));
    }
}
    
