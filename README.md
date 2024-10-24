# ping-monitor

1.实现基于zookeeper的 key、value 配置模块，用于拨测机器人读取配置<br>
2.拨测链式请求探针，使用函数式编程思路，插件方式<br>
3.提供一套 api 用于方便拨测机器人开发各种拨测插件<br>


程序模块：ping-manager(内勤zookeeper服务端，或者配置外界zk均可，负责任务分配，协调)后台，ping-rebot，ui

	ping-job-grop(拨测任务点) {

	ping-cycle 拨测调度周期:
	ping-robot {
		name@addr@procid {
			task {
				ping-job-01
			}
			plugins {

			}
		}
		name@addr@procid {
			task {
				ping-job-02
			}
			plugins {

			}
		}
	}

	plugins {

	}

	jobs {
		ping-job-01 {
			url: xxxxxx,
			plugin-name: xxxxx
		}，
		ping-job-02 {
			url: xxxxxx,
			plugin-name: xxxxx
		}，
		ping-job-03 {
			url: xxxxxx,
			plugin-name: xxxxx
		}
	}
	}


https://chatgpt.com/share/f3cb24b1-b8cd-4cec-b370-84073f813672
