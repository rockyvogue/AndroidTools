{
"main":[
{"name":"contact", "data":["[<CommonHead>] 给 [\"<unk>\"] (<Contact>) (<CallPost>)","[<CommonHead>] (<CallPre>) [\"<unk>\"] (<Contact>) [[的] (<PhoneType>)]","[<CommonHead>] 给 [\"<unk>\"] (<Contact>) [\"<unk>\"] (<Send> [一 个|一 条|个|条] <Message>)","[<CommonHead>] (<Send> [一 个|一 条|条|个] <Message> [给|到]) [\"<unk>\"] (<Contact>) [[的] (<PhoneType>)]"]},
{"name":"app", "data": ["[<CommonHead>|<APPHead>] (<Launch>) [\"<unk>\"] (<Apps>)","[<CommonHead>|<APPHead>] [\"<unk>\"] (把|将) (<Apps>) (<Launch>)","[<CommonHead>|<APPHead>] (<Uninstall>) [\"<unk>\"] (<Apps>)", "[<CommonHead>|<APPHead>] (把|将) [\"<unk>\"] (<Apps>) (<Uninstall>)","[<CommonHead>|<APPHead>] (<KillApp>) [\"<unk>\"] (<Apps>)","[<CommonHead>|<APPHead>] (把|将) [\"<unk>\"] (<Apps>) (<KillApp>)"]},
{"name":"setting","regexp":"true","data": ["<Setting>"]},
{"name":"music","data":["<PlayRandomMusic>","[<CommonHead>|<MusicHead>] (<PlayMusic>) [一|几] [首|曲|个|一下] [\"<unk>\"] [(<Singer>) (的|唱 的|版 的)] [\"<unk>\"] (<MusicTail>|([<MusicTail>](<Song>)))"]},
{"name":"fm", "data":["[<CommonHead>] (<Tuner>) [\"<unk>\"](((<FMInt>) [点 (<FMFloat>)] [兆 赫|兆|赫 兹])|((<AMInt>) [千 赫|赫 兹]))"]},
{"name":"command","define": "true","regexp":"true", "data":["<Command>"]},
{"name":"confirm","define": "true","regexp":"true", "data":["<Confirm>"]},
{"name":"custom", "define": "true","regexp":"true", "data":["<Custom>"]}
],

"tag":[
{"name":"CommonHead","data":["我想","我要","我想要","我需要","帮我","请帮我","替我","请替我","帮忙","请"]},
{"name":"APPHead","data":["给我","请给我"]},
{"name":"MusicHead","data":["随机","随便"]},
{"name":"MusicTail","data":["音乐","歌","歌曲","曲子"]},
{"name":"NaviHead","data":["带我","怎么"]},
{"name":"PhoneType","data":["号码","手机","手机号","手机号码","电话","电话号码","手提电话","移动电话","联系电话","联络电话","行动电话"]},
{"name":"Send","data":["去","写","编写","编辑","撰写","发","发送","回","回复"]}
],

"action":[
{"name":"Message","data":["信息","消息","文字讯息","简讯","短信","短信息","短消息"]},
{"name":"CallPre","data":["打电话给","打个电话给","打一个电话给","去电话给","去个电话给","去一个电话给","呼叫","呼电","拨","拨打","打","打给","拨给","拨打电话给","呼叫","拨通","接通","回","回复","回电话给","回个电话给","回一个电话给"]},
{"name":"CallPost","data":["打电话","打个电话","打一个电话","去电话","去个电话","去一个电话","去电","拨电话","拨个电话","拨一个电话","拨打电话","拨打一个电话","呼个电话","回电话","回个电话","回一个电话","回复电话","回复个电话","回复一个电话","电话"]},
{"name":"Launch","data":["加载","启动","打开","运行","玩","打开一下","运行一下","开启","用"]},
{"name":"Uninstall","data":["卸载","删除","移除","去除"]},
{"name":"KillApp","data":["关闭","停止","退出","停掉","关掉","关一下"]},
{"name":"PlayMusic","data":["听","播放","听听","放","播","点","点播","搜索","来"]},
{"name":"PlayRandomMusic","data":["音乐播放","歌曲播放"]},
{"name":"Nav","data":["回","导航到","到","去","导航回","导航去"]},
{"name":"Find","data":["找","查找","去","寻找","搜索"]},
{"name":"Tuner","data":["调频","调频到","收听","FM","AM","切换","调幅","调幅到","收听广播","收听电台","切换电台","切换广播"]}
],

"param":[
{"name":"call_contact","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.call\",\"code\":\"CALL\",\"semantic\":{\"intent\":{\"name\":\"{1}\"}},\"history\":\"cn.yunzhisheng.call\"}","action":["CallPre","CallPost"],"params":["MESSAGE","Contact"]},
{"name":"sms","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.sms\",\"code\":\"SMS_SEND\",\"semantic\":{\"intent\":{\"name\":\"{1}\"}},\"history\":\"cn.yunzhisheng.sms\"}","action":["Message"],"params":["MESSAGE","Contact"]},
{"name":"sms","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.sms\",\"code\":\"SMS_SEND\",\"semantic\":{\"intent\":{}},\"history\":\"cn.yunzhisheng.sms\"}","action":["Message"],"params":["MESSAGE"]},
{"name":"read_sms","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.sms\",\"code\":\"SMS_READ\",\"semantic\":{\"intent\":{\"name\":\"{1}\"}},\"history\":\"cn.yunzhisheng.sms\"}","action":["Read"],"params":["MESSAGE","Contact"]},

{"name":"launch_app","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.appmgr\",\"code\":\"APP_LAUNCH\",\"semantic\":{\"intent\":{\"name\":\"{1}\"}},\"history\":\"cn.yunzhisheng.appmgr\"}","action":["Launch"],"params":["MESSAGE","Apps"]},
{"name":"uninstall_app","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.appmgr\",\"code\":\"APP_UNINSTALL\",\"semantic\":{\"intent\":{\"name\":\"{1}\"}},\"history\":\"cn.yunzhisheng.appmgr\"}","action":["Uninstall"],"params":["MESSAGE","Apps"]},
{"name":"kill_app","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.appmgr\",\"code\":\"APP_EXIT\",\"semantic\":{\"intent\":{\"name\":\"{1}\"}},\"history\":\"cn.yunzhisheng.appmgr\"}","action":["KillApp"],"params":["MESSAGE","Apps"]},

{"name":"music_song_artist","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.music\",\"code\":\"SEARCH_SONG\",\"semantic\":{\"intent\":{\"artist\":\"{1}\",\"song\":\"{2}\",\"keyword\":\"{1} {2}\"}},\"history\":\"cn.yunzhisheng.music\"}","action":["PlayMusic"],"params":["MESSAGE","Singer","Song"]},
{"name":"music_artist","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.music\",\"code\":\"SEARCH_ARTIST\",\"semantic\":{\"intent\":{\"artist\":\"{1}\",\"keyword\":\"{1}\"}},\"history\":\"cn.yunzhisheng.music\"}","action":["PlayMusic"],"params":["MESSAGE","Singer"]},
{"name":"music_song","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.music\",\"code\":\"SEARCH_SONG\",\"semantic\":{\"intent\":{\"song\":\"{1}\",\"keyword\":\"{1}\"}},\"history\":\"cn.yunzhisheng.music\"}","action":["PlayMusic"],"params":["MESSAGE","Song"]},
{"name":"music_path_song","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.music\",\"code\":\"SEARCH_SONG\",\"semantic\":{\"intent\":{\"path\":\"{1}\",\"song\":\"{2}\",\"keyword\":\"{1} {2}\"}},\"history\":\"cn.yunzhisheng.music\"}","action":["PlayMusic"],"params":["MESSAGE","MusicPath","Song"]},
{"name":"music_path","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.music\",\"code\":\"SEARCH_SONG\",\"semantic\":{\"intent\":{\"path\":\"{1}\",\"keyword\":\"{1}\"}},\"history\":\"cn.yunzhisheng.music\"}","action":["PlayMusic"],"params":["MESSAGE","MusicPath"]},
{"name":"music_random","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.music\",\"code\":\"SEARCH_RANDOM\",\"history\":\"cn.yunzhisheng.music\"}","action":["PlayRandomMusic","PlayMusic"],"params":["MESSAGE"]},
{"name":"prev","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_PREV\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["上个"],["上首"],["上曲"],["上个歌"],["上首歌"],["上曲歌"],["上个歌曲"],["上首歌曲"],["上曲歌曲"],["上一歌"],["上一歌曲"],["前个歌"],["前首歌"],["前曲歌"],["前个歌曲"],["前首歌曲"],["前曲歌曲"],["前个频道"],["前一个频道"],["前一歌"],["上一频道"],["前一频道"],["上一电台"],["前一歌曲"],["上一个"],["上一个影片"],["上一个歌"],["上一个歌曲"],["上一个电影"],["上一个视频"],["上一个音乐"],["上一曲"],["上一曲歌"],["上一曲歌曲"],["上一曲音乐"],["上一部"],["上一部影片"],["上一部电影"],["上一部视频"],["上一首"],["上一首歌"],["上一首歌曲"],["上一首音乐"],["前一个"],["前一个影片"],["前一个歌"],["前一个歌曲"],["前一个电影"],["前一个视频"],["前一个音乐"],["前一曲"],["前一曲歌"],["前一曲歌曲"],["前一曲音乐"],["前一部"],["前一部影片"],["前一部电影"],["前一部视频"],["前一首"],["前一首歌"],["前一首歌曲"],["前一首音乐"],["听上一个"],["听上一个歌"],["听上一个歌曲"],["听上一个音乐"],["听上一曲"],["听上一曲歌"],["听上一曲歌曲"],["听上一曲音乐"],["听上一首"],["听上一首歌"],["听上一首歌曲"],["听上一首音乐"],["听前一个"],["听前一个歌"],["听前一个歌曲"],["听前一个音乐"],["听前一曲"],["听前一曲歌"],["听前一曲歌曲"],["听前一曲音乐"],["听前一首"],["听前一首歌"],["听前一首歌曲"],["听前一首音乐"],["播放上一个"],["播放上一个影片"],["播放上一个歌"],["播放上一个歌曲"],["播放上一个电影"],["播放上一个视频"],["播放上一个音乐"],["播放上一曲"],["播放上一曲歌"],["播放上一曲歌曲"],["播放上一曲音乐"],["播放上一部"],["播放上一部影片"],["播放上一部电影"],["播放上一部视频"],["播放上一首"],["播放上一首歌"],["播放上一首歌曲"],["播放上一首音乐"],["播放前一个"],["播放前一个影片"],["播放前一个歌"],["播放前一个歌曲"],["播放前一个电影"],["播放前一个视频"],["播放前一个音乐"],["播放前一曲"],["播放前一曲歌"],["播放前一曲歌曲"],["播放前一曲音乐"],["播放前一部"],["播放前一部影片"],["播放前一部电影"],["播放前一部视频"],["播放前一首"],["播放前一首歌"],["播放前一首歌曲"],["播放前一首音乐"],["看上一个"],["看上一个影片"],["看上一个电影"],["看上一个视频"],["看上一部"],["看上一部影片"],["看上一部电影"],["看上一部视频"],["看前一个"],["看前一个影片"],["看前一个电影"],["看前一个视频"],["看前一部"],["看前一部影片"],["看前一部电影"],["看前一部视频"],["观看上一个"],["观看上一个影片"],["观看上一个电影"],["观看上一个视频"],["观看上一部"],["观看上一部影片"],["观看上一部电影"],["观看上一部视频"],["观看前一个"],["观看前一个影片"],["观看前一个电影"],["观看前一个视频"],["观看前一部"],["观看前一部影片"],["观看前一部电影"],["观看前一部视频"]]},
{"name":"next","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_NEXT\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["下个"],["下首"],["下个歌"],["下首歌"],["下曲歌"],["下个歌曲"],["下首歌曲"],["下曲歌曲"],["后个歌"],["后首歌"],["后曲歌"],["后个歌曲"],["后首歌曲"],["后曲歌曲"],["下一歌"],["后一歌"],["下一歌曲"],["后一歌曲"],["下一个"],["下一个影片"],["下一个歌"],["下一个歌曲"],["下一个电影"],["下一个视频"],["下一个音乐"],["下一曲"],["下一曲歌"],["下一曲歌曲"],["下一曲音乐"],["下一部"],["下一部影片"],["下一部电影"],["下一部视频"],["下一首"],["下一首歌"],["下一首歌曲"],["下一首音乐"],["后一个"],["后一个影片"],["后一个歌"],["后一个歌曲"],["后一个电影"],["后一个视频"],["后一个音乐"],["后一曲"],["后一曲歌"],["后一曲歌曲"],["后一曲音乐"],["后一部"],["后一部影片"],["后一部电影"],["后一部视频"],["后一首"],["后一首歌"],["后一首歌曲"],["后一首音乐"],["听下一个"],["听下一个歌"],["听下一个歌曲"],["听下一个音乐"],["听下一曲"],["听下一曲歌"],["听下一曲歌曲"],["听下一曲音乐"],["听下一首"],["听下一首歌"],["听下一首歌曲"],["听下一首音乐"],["听后一个"],["听后一个歌"],["听后一个歌曲"],["听后一个音乐"],["听后一曲"],["听后一曲歌"],["听后一曲歌曲"],["听后一曲音乐"],["听后一首"],["听后一首歌"],["听后一首歌曲"],["听后一首音乐"],["播放下一个"],["播放下一个影片"],["播放下一个歌"],["播放下一个歌曲"],["播放下一个电影"],["播放下一个视频"],["播放下一个音乐"],["播放下一曲"],["播放下一曲歌"],["播放下一曲歌曲"],["播放下一曲音乐"],["播放下一部"],["播放下一部影片"],["播放下一部电影"],["播放下一部视频"],["播放下一首"],["播放下一首歌"],["播放下一首歌曲"],["播放下一首音乐"],["播放后一个"],["播放后一个影片"],["播放后一个歌"],["播放后一个歌曲"],["播放后一个电影"],["播放后一个视频"],["播放后一个音乐"],["播放后一曲"],["播放后一曲歌"],["播放后一曲歌曲"],["播放后一曲音乐"],["播放后一部"],["播放后一部影片"],["播放后一部电影"],["播放后一部视频"],["播放后一首"],["播放后一首歌"],["播放后一首歌曲"],["播放后一首音乐"],["看下一个"],["看下一个影片"],["看下一个电影"],["看下一个视频"],["看下一部"],["看下一部影片"],["看下一部电影"],["看下一部视频"],["看后一个"],["看后一个影片"],["看后一个电影"],["看后一个视频"],["看后一部"],["看后一部影片"],["看后一部电影"],["看后一部视频"],["观看下一个"],["观看下一个影片"],["观看下一个电影"],["观看下一个视频"],["观看下一部"],["观看下一部影片"],["观看下一部电影"],["观看下一部视频"],["观看后一个"],["观看后一个影片"],["观看后一个电影"],["观看后一个视频"],["观看后一部"],["观看后一部影片"],["观看后一部电影"],["观看后一部视频"]]},
{"name":"pause","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_PAUSE\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["暂停"],["暂停播放"],["播放暂停"],["暂停音乐"],["暂停歌曲"],["音乐暂停"],["歌曲暂停"]]},
{"name":"stop","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_STOP\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["停止"],["停止播放"],["停止音乐"],["停止歌曲"],["音乐停止"],["歌曲停止"],["取消播放"]]},
{"name":"resume","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_RESUME\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["恢复"],["恢复播放"],["继续"],["继续播放"],["接着放"],["继续播放音乐"],["继续播放歌曲"],["音乐继续播放"],["歌曲继续播放"]]},
{"name":"play","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_PLAY\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["播放"],["取消暂停"]]},
{"name":"close","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_CLOSE\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["关闭"],["退出"]]},
#{"name":"back","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_BACK\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["返回"],["后退"],["回退"]]},

{"name":"city","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.city\",\"code\":\"CHANGE_CITY\",\"semantic\":{\"intent\":{\"city\":\"{1}\"}},\"history\":\"cn.yunzhisheng.city\"}","action":[],"params":["MESSAGE","City"]},
{"name":"local_business_search","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.localsearch\",\"code\":\"BUSINESS_SEARCH\",\"semantic\":{\"intent\":{\"category\":\"{1}\",\"city\":\"CURRENT_CITY\",\"poi\": \"CURRENT_LOC\"}},\"history\":\"cn.yunzhisheng.localsearch\"}","action":["Find"],"params":["MESSAGE","BusinessCategory"]},
{"name":"nav_favorite_poi","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.map\",\"code\":\"FAVORITE_ROUTE\",\"semantic\":{\"intent\":{\"fromPOI\":\"CURRENT_LOC\",\"fromCity\":\"CURRENT_CITY\",\"toPOI\":\"{1}\"}},\"history\":\"cn.yunzhisheng.map\"}","action":["Nav"],"params":["MESSAGE","FavoritePOI"]},

{"name":"broadcast1","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.broadcast\",\"semantic\":{\"intent\":{\"channelList\":[{\"frequencyList\":[{\"frequency\":\"{1}.{2}\",\"type\":\"FM\",\"unit\":\"MHz\"}]}]}},\"history\":\"cn.yunzhisheng.broadcast\"}","action":["Tuner"],"params":["MESSAGE","FMInt","FMFloat"]},
{"name":"broadcast2","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.broadcast\",\"semantic\":{\"intent\":{\"channelList\":[{\"frequencyList\":[{\"frequency\":\"{1}\",\"type\":\"FM\",\"unit\":\"MHz\"}]}]}},\"history\":\"cn.yunzhisheng.broadcast\"}","action":["Tuner"],"params":["MESSAGE","FMInt"]},
{"name":"broadcast3","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.broadcast\",\"semantic\":{\"intent\":{\"channelList\":[{\"frequencyList\":[{\"frequency\":\"{1}\",\"type\":\"AM\",\"unit\":\"KHz\"}]}]}},\"history\":\"cn.yunzhisheng.broadcast\"}","action":["Tuner"],"params":["MESSAGE","AMInt"]},

{"name":"open_3g","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_OPEN\",\"operands\":\"OBJ_3G\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["打开移动网络"],["打开网络"],["打开网络连接"],["打开3G"],["打开4G"],["打开GPRS"],["打开数据"],["打开移动数据"]]},
{"name":"close_3g","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_CLOSE\",\"operands\":\"OBJ_3G\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["关闭移动网络"],["关闭网络"],["关闭网络连接"],["关闭3G"],["关闭4G"],["关闭GPRS"],["关闭数据"],["关闭移动数据"]]},
{"name":"open_bluetooth","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_OPEN\",\"operands\":\"OBJ_BLUETOOTH\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["打开蓝牙"],["开启蓝牙"],["启动蓝牙"],["蓝牙打开"],["蓝牙开启"],["蓝牙启动"]]},
{"name":"close_bluetooth","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_CLOSE\",\"operands\":\"OBJ_BLUETOOTH\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["关闭蓝牙"],["关掉蓝牙"],["停止蓝牙"],["蓝牙关闭"],["蓝牙关掉"],["蓝牙停止"],["退出蓝牙"]]},
{"name":"open_gps","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_OPEN\",\"operands\":\"OBJ_GPS\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["打开GPS"],["打开定位"],["开启定位"],["开启GPS"]]},
{"name":"close_gps","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_CLOSE\",\"operands\":\"OBJ_GPS\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["关闭GPS"],["关闭定位"],["关掉定位"],["关掉GPS"]]},
{"name":"set_time","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_SET\",\"operands\":\"OBJ_TIME\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["设置时间"],["设置系统时间"],["调整时间"],["调整系统时间"],["校准时间"],["校准系统时间"]]},

{"name":"open_mute","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_OPEN\",\"operands\":\"OBJ_MODEL_MUTE\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["静音"],["静音模式"],["打开静音"],["开启静音"],["启动静音"],["打开静音模式"],["开启静音模式"],["启动静音模式"],["静音打开"],["静音开启"],["静音启动"],["静音模式打开"],["静音模式开启"],["静音模式启动"],["关闭声音"],["关闭音量"],["音量关闭"],["声音关闭"],["音量最小"],["声音最小"],["声音调到最小"],["音量调到最小"],["声音调为最小"],["音量调为最小"]]},
{"name":"close_mute","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_CLOSE\",\"operands\":\"OBJ_MODEL_MUTE\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["关闭静音"],["取消静音"],["退出静音"],["关闭静音模式"],["取消静音模式"],["退出静音模式"],["打开声音"],["打开音量"],["音量打开"],["声音打开"]]},

{"name":"open_wifi","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_OPEN\",\"operands\":\"OBJ_WIFI\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["打开wifi"],["开启wifi"],["启动wifi"],["wifi打开"],["wifi开启"],["wifi启动"],["打开无线"],["打开无线网"],["打开无线路由"],["开启无线"],["开启无线路由"],["启动无线"],["启动无线路由"],["无线打开"],["无线路由打开"],["无线开启"],["无线路由开启"],["无线启动"],["无线路由启动"]]},
{"name":"close_wifi","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_CLOSE\",\"operands\":\"OBJ_WIFI\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["关闭wifi"],["关掉wifi"],["取消wifi"],["wifi关闭"],["wifi关掉"],["wifi取消"],["关闭无线"],["关闭无线路由"],["关掉无线"],["关掉无线路由"],["取消无线"],["取消无线路由"],["无线关闭"],["无线路由关闭"],["无线关掉"],["无线路由关掉"],["无线取消"],["无线路由取消"]]},

{"name":"increase_volume","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_INCREASE\",\"operands\":\"OBJ_VOLUMN\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["大声"],["大声点"],["大点声"],["声音高"],["声音加"],["音量高"],["音量加"],["声音太小了"],["声音太小"],["声音小了"],["音量太小了"],["音量太小"],["音量小了"],["声音大一点"],["声音再大点"],["声音稍微大一点"],["音量大一点"],["音量再大点"],["音量稍微大一点"],["大点声音"],["大点音量"],["声音大点"],["音量大点"],["声音稍微大点"],["音量稍微大点"],["调大声音"],["调大音量"],["声音调大"],["音量调大"],["声音调大点"],["音量调大点"],["声音调大一点"],["音量调大一点"],["声音稍微调大一点"],["音量稍微调大一点"],["放大声音"],["放大音量"],["声音放大"],["音量放大"],["声音放大点"],["音量放大点"],["声音放大一点"],["加大声音"],["加大音量"],["声音加大"],["音量加大"],["声音加大点"],["音量加大点"],["声音加大一点"],["增大声音"],["增大音量"],["声音增大"],["音量增大"],["声音增大点"],["音量增大点"],["声音增大一点"],["增强声音"],["增强音量"],["声音增强"],["音量增强"],["声音增强点"],["音量增强点"],["声音增强一点"],["增加声音"],["增加音量"],["声音增加"],["音量增加"],["声音增加点"],["音量增加点"],["声音增加一点"],["调高声音"],["调高音量"],["声音调高"],["音量调高"],["声音调高点"],["音量调高点"],["声音调高一点"],["音量调高一点"],["声音稍微调高一点"],["音量稍微调高一点"],["音量增加一点"],["声音稍微增加一点"],["音量稍微增加一点"],["音量增强一点"],["声音稍微增强一点"],["音量稍微增强一点"],["音量增大一点"],["声音稍微增大一点"],["音量稍微增大一点"],["音量加大一点"],["声音稍微加大一点"],["音量稍微加大一点"],["音量放大一点"],["声音稍微放大一点"],["音量稍微放大一点"],["声音调到最大"],["调到最大声音"],["加音量"],["增高音量"],["音量增高"],["增高声音"],["声音增高"]]},
{"name":"decrease_volume","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_DECREASE\",\"operands\":\"OBJ_VOLUMN\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["小声"],["小声点"],["小点声"],["声音太大了"],["声音太大"],["声音大了"],["音量太大了"],["音量太大"],["音量大了"],["声音小一点"],["声音再小点"],["声音稍微小一点"],["音量小一点"],["音量再小点"],["音量稍微小一点"],["小点声音"],["小点音量"],["声音小点"],["音量小点"],["声音稍微小点"],["音量稍微小点"],["调小声音"],["调小音量"],["声音调小"],["音量调小"],["声音调小点"],["音量调小点"],["声音调小一点"],["音量调小一点"],["声音稍微调小一点"],["音量稍微调小一点"],["放小声音"],["放小音量"],["声音放小"],["音量放小"],["声音放小点"],["音量放小点"],["声音放小一点"],["音量放小一点"],["声音稍微放小一点"],["音量稍微放小一点"],["减小声音"],["减小音量"],["声音减小"],["音量减小"],["声音减小点"],["音量减小点"],["声音减小一点"],["音量减小一点"],["声音稍微减小一点"],["音量稍微减小一点"],["减弱声音"],["减弱音量"],["声音减弱"],["音量减弱"],["声音减弱点"],["音量减弱点"],["声音减弱一点"],["音量减弱一点"],["声音稍微减弱一点"],["音量稍微减弱一点"],["降低声音"],["降低音量"],["声音降低"],["音量降低"],["声音降低点"],["音量降低一点"],["声音降低点"],["音量降低一点"],["声音稍微降低点"],["音量稍微降低一点"],["调低音量"],["音量调低"],["音量减"],["减音量"]]},
{"name":"volume_max","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_MAX\",\"operands\":\"OBJ_VOLUMN\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["声音最大"],["音量最大"],["声音调到最大"],["音量调到最大"],["声音调为最大"],["音量调为最大"]]},

{"name":"open_wifi_spot","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_OPEN\",\"operands\":\"OBJ_WIFI_SPOT\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["打开wifi热点"],["开启wifi热点"],["启动wifi热点"],["打开无线热点"],["开启无线热点"],["启动无线热点"]]},
{"name":"close_wifi_spot","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_CLOSE\",\"operands\":\"OBJ_WIFI_SPOT\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["关闭wifi热点"],["关掉wifi热点"],["退出wifi热点"],["取消wifi热点"],["关闭无线热点"],["关掉无线热点"],["退出无线热点"],["取消无线热点"]]},

{"name":"select_1","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"select\":\"1\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Command"],"Command":[["第一个"],["第一行"],["第一条"],["第个"],["第行"],["第条"],["滴个"],["滴行"],["滴条"]]},
{"name":"select_2","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"select\":\"2\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Command"],"Command":[["第二个"],["第二行"],["第二条"]]},
{"name":"select_3","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"select\":\"3\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Command"],"Command":[["第三个"],["第三行"],["第三条"]]},
{"name":"select_4","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"select\":\"4\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Command"],"Command":[["第四个"],["第四行"],["第四条"]]},
{"name":"select_5","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"select\":\"5\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Command"],"Command":[["第五个"],["第五行"],["第五条"]]},
{"name":"select_6","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"select\":\"6\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Command"],"Command":[["第六个"],["第六行"],["第六条"]]},
{"name":"select_7","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"select\":\"7\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Command"],"Command":[["第七个"],["第七行"],["第七条"]]},
{"name":"select_8","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"select\":\"8\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Command"],"Command":[["第八个"],["第八行"],["第八条"]]},
{"name":"select_9","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"select\":\"9\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Command"],"Command":[["第九个"],["第九行"],["第九条"]]},
{"name":"select_10","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"select\":\"10\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Command"],"Command":[["第十个"],["第十行"],["第十条"]]},
{"name":"select_last","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"select\":\"-1\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Command"],"Command":[["最后一个"],["最后一行"],["最后一条"]]},

{"name":"select_page_1","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"select\":\"101\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Command"],"Command":[["下一页"]]},
{"name":"select_page_1","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"select\":\"100\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Command"],"Command":[["上一页"]]},

{"name":"confirm_cancel","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"confirm\":\"cancel\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["取消"]]},

{"name":"locate","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.map\",\"code\":\"POSITION\",\"semantic\":{\"intent\":{\"toPOI\":\"CURRENT_LOC\",\"toCity\":\"CURRENT_CITY\"}},\"history\":\"cn.yunzhisheng.map\"}","action":[],"params":["MESSAGE","Command"],"Command":[["我现在在哪"],["我现在在哪里"],["我在哪里"],["我在哪儿"],["查询地图"],["查地图"],["我的位置"],["查看我的位置"],["查看我的地址"],["查看我现在在哪"],["查看当前位置"],["查询当前位置"],["查询我的位置"],["查询我的地址"],["查询我现在在哪"],["查询我现在在哪儿"],["定位当前位置"],["打开我的位置"],["打开位置"]]},

{"name":"confirm_ok","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"confirm\":\"ok\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Confirm"],"Confirm":[["确定"]]},
{"name":"confirm_cancel","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"confirm\":\"cancel\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Confirm"],"Confirm":[["取消"]]},
{"name":"confirm_send","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"confirm\":\"send\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Confirm"],"Confirm":[["发送"]]},
{"name":"confirm_clean","protocal":"{\"service\":\"DOMAIN_LOCAL\",\"confirm\":\"clean\",\"message\":\"{0}\"}","action":[],"params":["MESSAGE","Confirm"],"Confirm":[["清空"],["重新输入"]]},

{"name":"redial","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_REDIAL\",\"operands\":\"OBJ_CALL\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Command"],"Command":[["回拨电话"],["重拨"],["重新拨号"],["重拨电话"]]},
{"name":"answer_call","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_ANSWER\",\"operands\":\"OBJ_CALL\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Command"],"Command":[["接听"],["接听电话"],["接电话"]]},
{"name":"hung_up_call","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_HANG_UP\",\"operands\":\"OBJ_CALL\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Command"],"Command":[["挂断"],["挂了"],["挂断电话"],["挂断来电"],["拒接电话"],["拒接"]]},

{"name":"bluetooth_match","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_MATCH\",\"operands\":\"OBJ_BLUETOOTH\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["蓝牙匹配"],["匹配蓝牙"],["连接蓝牙设备"]]},
{"name":"bluetooth_disconnected","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_DISCONNECTED\",\"operands\":\"OBJ_BLUETOOTH\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["断开蓝牙连接"],["删除手机"]]},

{"name":"music_loop_cancel","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_CANCEL\",\"operands\":\"OBJ_MUSIC_LOOP\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["取消循环播放"]]},
{"name":"music_shuffle_playback_cancel","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_CANCLE\",\"operands\":\"OBJ_MUSIC_SHUFFLE_PLAYBACK\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["取消随机播放"]]},
{"name":"music_order_playback","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_PLAY\",\"operands\":\"OBJ_MUSIC_ORDER_PLAYBACK\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["顺序播放"]]},
{"name":"music_single_cycle","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_PLAY\",\"operands\":\"OBJ_MUSIC_SINGLE_CYCLE\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["单曲重复"],["单曲循环"]]},
{"name":"music_list_cycle","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_PLAY\",\"operands\":\"OBJ_MUSIC_LIST_CYCLE\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["列表循环"],["列表重复"]]},
{"name":"music_shuffle_playback","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_PLAY\",\"operands\":\"OBJ_MUSIC_SHUFFLE_PLAYBACK\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["随机播放"]]},
{"name":"music_full_cycle","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_PLAY\",\"operands\":\"OBJ_MUSIC_FULL_CYCLE\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["全部循环"]]},
{"name":"music_open_lyric","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_OPEN_LYRIC\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["打开桌面歌词"]]},
{"name":"music_close_lyric","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_CLOSE_LYRIC\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["关闭桌面歌词"]]},
{"name":"music_close_kwapp","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_CLOSE_KWAPP\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["关闭酷我"],["关闭酷我音乐"],["退出酷我"],["退出酷我音乐"],["暂停音乐"]]},

{"name":"goto_desktop","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_GOTO_HOME\",\"operands\":\"OBJ_GOTO_HOME\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["主界面"],["返回主界面"],["回到主界面"],["退回主界面"],["桌面"],["返回桌面"],["回到桌面"],["退回桌面"],["主页"],["返回主页"],["回到主页"],["退回主页"],["HOME"],["返回HOME"],["回到HOME"],["退回HOME"]]},
{"name":"launch_nav","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_LAUNCHER_NAVI\",\"operands\":\"OBJ_LAUNCHER_NAVI\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["打开导航"],["打开百度导航"],["导航"],["百度导航"],["我的导航"],["我的位置"]]},
{"name":"open_record_video","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_OPEN_RECORD_VIDEO\",\"operands\":\"OBJ_OPEN_RECORD_VIDEO\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["打开视频"],["打开行车记录视频"],["行车记录视频"],["行车记录的视频"],["视频"],["视频回放"],["播放视频"],["浏览视频"],["回放视频"],["打开视频文件"],["我要看视频"],["看视频"]]},
{"name":"open_record_photo","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_OPEN_RECORD_PHOTO\",\"operands\":\"OBJ_OPEN_RECORD_PHOTO\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["打开图片"],["图片"],["行车记录图片"],["图片"],["图片回放"],["播放图片"],["浏览图片"],["回放图片"],["打开图片文件"],["我要看图片"],["看图片"]]},
{"name":"do_take_photo","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_DO_TAKE_PHOTO\",\"operands\":\"OBJ_DO_TAKE_PHOTO\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["拍照"],["照相"],["照一张图片"],["抓拍"]]},
{"name":"open_user_center","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_OPEN_USER_CENTER\",\"operands\":\"OBJ_OPEN_USER_CENTER\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["个人中心"],["打开个人中心"],["个人"],["中心"]]},

{"name":"increase_brightness","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_INCREASE\",\"operands\":\"OBJ_LIGHT\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["增加亮度"],["亮度增加"],["屏幕亮"]]},
{"name":"decrease_brightness","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_DECREASE\",\"operands\":\"OBJ_LIGHT\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["减小亮度"],["亮度减小"],["屏幕暗"]]},

{"name":"open_front_camera","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_OPEN\",\"operands\":\"OBJ_FRONTCAMERA\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["打开前置摄像头"]]},
{"name":"open_real_camera","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_OPEN\",\"operands\":\"OBJ_REARCAMERA\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["打开后置摄像头"]]},

{"name":"open_carcorder","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_OPEN\",\"operands\":\"OBJ_CARCORDER\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Setting"],"Setting":[["打开行车记录仪"],["打开行车记录"],["行车记录仪打开"],["行车记录打开"],["打开记录仪"],["记录仪打开"],["行车记录仪"],["行车记录"]]},
{"name":"play","protocal":"{\"rc\":0,\"text\":\"{0}\",\"service\":\"cn.yunzhisheng.setting\",\"code\":\"SETTING_EXEC\",\"semantic\":{\"intent\":{\"operator\":\"ACT_PLAY_MESSAGE\"}},\"history\":\"cn.yunzhisheng.setting\"}","action":[],"params":["MESSAGE","Command"],"Command":[["播报"],["播报短信"]]}
],

"custom":[
{"name":"FavoritePOI","data":[["公司"],["家"]]},
{"name":"BusinessCategory", "data":[["加油站"],["餐馆"],["停车场"],["咖啡厅"],["酒吧"],["自动提款机"],["提款机"],["ATM"],["ATM机"],["杂货店"],["药店"],["购物"],["医院"],["超市"],["银行"],["公交车站"],["停车场"],["机场"],["火车站"],["KTV"],["书店"],["饭店"],["加油站"],["中国石化"],["中国石油"],["洗车场"],["汽车养护店"],["汽车养护"],["4S店"],["汽车销售"],["汽车维修中心"],["汽车维修"],["餐厅"],["西餐厅"],["中餐厅"],["湘菜馆"],["火锅店"],["川菜馆"],["外国餐厅"],["日本料理"],["韩国料理"],["肯德基"],["麦当劳"],["必胜客"],["辛巴克咖啡"],["商场"],["购物中心"],["家乐福"],["沃尔玛"],["学校"],["宾馆"],["酒店"],["自动提款机"],["中国银行"],["交通银行"],["工商银行"],["招商银行"],["建设银行"],["农业银行"],["交通局"],["保险中心"],["厕所"],["公共厕所"],["电影院"]]},
{"name":"MusicPath","data":[["CD机"],["CD"],["本地"],["TF卡"],["手机"],["优盘"],["U盘"],["SD卡"],["IPHONE"],["IPOD"],["IPAD"]]},
{"name":"FMInt","data":[["87"],["88"],["89"],["90"],["91","九幺"],["92"],["93"],["94"],["95"],["96"],["97"],["98"],["99"],["100","幺零零"],["101","幺零幺"],["102","幺零二"],["103", "幺零三"],["104","幺零四"],["105","幺零五"],["106","幺零六"],["107","幺零七"],["108","幺零八"]]},
{"name":"FMFloat","data":[["0"],["1","幺"],["2"],["3"],["4"],["5"],["6"],["7"],["8"],["9"]]},
{"name":"AMInt","data":[["531","五三幺"],["540"],["549"],["558"],["567"],["576"],["585"],["594"],["603"],["612","六幺二"],["621","六二幺"],["630"],["639"],["648"],["657"],["666"],["675"],["684"],["693"],["702"],["711","七幺幺"],["720"],["729"],["738"],["747"],["756"],["765"],["774"],["783"],["792"],["801","八零幺"],["810","八幺零","八百一"],["819","八幺九"],["828"],["837"],["846"],["855"],["864"],["873"],["882"],["891","八九幺"],["900"],["909"],["918","九幺八"],["927"],["936"],["945"],["954"],["963"],["972"],["981","九八幺"],["990"],["999"],["1008","幺零零八"],["1017","幺零幺七"],["1026","幺零二六"],["1035","幺零三五"],["1044","幺零四四"],["1053","幺零五三"],["1062","幺零六二"],["1071","幺零七幺"],["1080","幺零八零"],["1089","幺零八九"],["1098","幺零九八"],["1107","幺幺零七"],["1116","幺幺幺六"],["1125","幺幺二五"],["1134","幺幺三四"],["1143","幺幺四三"],["1152","幺幺五二"],["1161","幺幺六幺"],["1170","幺幺七零"],["1179","幺幺七九"],["1188","幺幺八八"],["1197","幺幺九七"],["1206","幺二零六","一千两百零六"],["1215","幺二幺五","一千两百一十五"],["1224","幺二二四","一千两百二十四"],["1233","幺二三三","一千两百三十三"],["1242","幺二四二","一千两百四十二"],["1251","幺二五幺","一千两百五十一"],["1260","幺二六零","一千两百六十"],["1269","幺二六九","一千两百六十九"],["1278","幺二七八","一千两百七十八"],["1287","幺二八七","一千两百八十七"],["1296","幺二九六","一千两百九十六"],["1305","幺三零五"],["1314","幺三幺四"],["1323","幺三二三"],["1332","幺三三二"],["1341","幺三四幺"],["1350","幺三五零"],["1359","幺三五九"],["1368","幺三六八"],["1377","幺三七七"],["1386","幺三八六"],["1395","幺三九五"],["1404","幺四零四"],["1413","幺四幺三"],["1422","幺四二二"],["1431","幺四三幺"],["1440","幺四四零"],["1449","幺四四九"],["1458","幺四五八"],["1467","幺四六七"],["1476","幺四七六"],["1485","幺四八五"],["1494","幺四九四"],["1503","幺五零三"],["1512","幺五幺二"],["1521","幺五二幺"],["1530","幺五三零"],["1539","幺五三九"],["1548","幺五四八"],["1557","幺五五七"],["1566","幺五六六"],["1575","幺五七五"],["1584","幺五八四"],["1593","幺五九三"],["1602","幺六零二"],["1611","幺六幺幺"],["1620","幺六二零"],["1629","幺六二九"]]}
]
}
