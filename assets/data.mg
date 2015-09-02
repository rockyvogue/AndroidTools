project_vendor=vui
project_type=vui_car_assistant
# ASR
asr_service_key=-116,-110,-115,-125,-119,-131,-117,-122,-117,-119,-128,-125,-77,-145,-151,-81,-141,-85,-147,-147,-93,-97,-97,-152,-158,-169,-106,-176,-166,-181,-167,-167,-180,-120,-189,-124,-126,-186,-174,-182
asr_recognizer_domain=poi
recording_mute=true
recording_timeout=10000
vad_timeout_after_talk=1000
net_get_result_timeout=5000
# ASR FIX
offline_no_result_protocal={"rc":1001,"text":"{0}","service":"cn.yunzhisheng.error","general":{"type":"T","text":"没听懂你的话，请再说一遍"},"history":"cn.yunzhisheng.error"}
offline_error_result_protocal={"rc":1002,"text":"{0}","service":"cn.yunzhisheng.error","general":{"type":"T","text":"对不起，我不明白您说的意思，如果有网络的话，我会表现的比现在更好哦！"},"history":"cn.yunzhisheng.error"}
offline_unreliable_result_protocal={"rc":1003,"text":"{0}","service":"cn.yunzhisheng.error","general":{"type":"T","text":"我不确定您说的是什么，如果有网络的话，我会表现的更好哦！"},"history":"cn.yunzhisheng.error"}
offline_recognition_benchmark=-5
loose_offline_recognition_benchmark=-6.7
front_reset_cache_byte_time=300
# NLU 
nlu_service_domain=-105,-119,-121,-119,-67,-58,-60,-130,-116,-137,-71,-69,-129,-132,-147,-142,-138,-134,-138,-85,-140,-153
nlu_service_appkey=-116,-110,-115,-125,-119,-131,-117,-122,-117,-119,-128,-125,-77,-145,-151,-81,-141,-85,-147,-147,-93,-97,-97,-152,-158,-169,-106,-176,-166,-181,-167,-167,-180,-120,-189,-124,-126,-186,-174,-182
nlu_service_secret=-101,-60,-62,-58,-57,-60,-61,-116,-119,-69,-69,-78,-75,-78,-129,-81,-133,-84,-139,-87,-141,-142,-101,-97,-146,-99,-109,-104,-114,-115,-111,-165
nlu_connect_timeout=3000
nlu_read_timeout=3000
nlu_param_scenario=incar
# Wakeup
wakeup_command=你好魔方
wakeup_benchmark=-2.8
baidu_key=-53,-52,-53,-109,-111,-67,-70,-66,-67,-72,-70,-73,-79,-82,-86,-84,-135,-136,-139,-95,-139,-140,-145,-101,-148,-150,-102,-103,-156,-157,-112,-120
dianping_app_key=-50,-59,-62,-63,-65,-65,-65,-69,-70,-72
dianping_app_secret=-101,-59,-60,-60,-109,-63,-110,-72,-65,-67,-69,-120,-77,-128,-78,-84,-90,-135,-94,-95,-90,-140,-144,-96,-148,-108,-150,-107,-156,-159,-113,-161
vui_type=VUI_TYPE_RECOGNIZER
vui_status=VUI_STATUS_NOMAL
location_vendor=BAIDU
poi_vendor=BAIDU
media_type=SYSTEM
scene_switch=ON

