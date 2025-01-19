package dev.combooo.okx;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OkxService {
    private static final String OKX_BASE_URL = "https://www.okx.com";
    private static final String KLINE_ENDPOINT = "/api/v5/market/candles";

    private static final String PROXY_HOST = "127.0.0.1";
    private static final int PROXY_PORT = 10809;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String getCandleStickData(String pair, String minute, String limit) {
        if (StrUtil.isBlank(pair)) {
            throw new BizException("请输入交易对名称");
        }

        if (StrUtil.isBlank(minute)) {
            throw new BizException("请输入时间级别");
        }


        int limit0 = 10;//默认10根
        try {
            limit0 = Integer.parseInt(limit);
        } catch (Exception e) {
            throw new BizException("错误的根数参数=》" + limit);
        }

        if (limit0 <= 0 || limit0 > 300) {
            throw new BizException("limit 参数必须是 1 到 300 之间的整数");
        }

        String url = OKX_BASE_URL + KLINE_ENDPOINT;

        Map<String, Object> params = new HashMap<>();
        params.put("instId", pair); // 交易对
        params.put("bar", minute);         // 15分钟K线
        params.put("limit", limit);       // 数据条数


        String result = HttpUtil
                .createGet(url)
                .setHttpProxy(PROXY_HOST, PROXY_PORT)
                .form(params)
                .execute()
                .body();
        JSONObject obj = JSONUtil.parseObj(result);

        // 判断请求是否成功
        String code = obj.getStr("code");
        if (!"0".equals(code)) {
            throw new BizException("请求失败，返回错误码：" + code + ", 错误信息：" + obj.getStr("msg"));
        }

        JSONArray dataArray = obj.getJSONArray("data");
        if (dataArray != null) {
            for (int i = dataArray.size() -1; i >= 0; i--){ // 从最新的数据开始倒序遍历
                JSONArray data = (JSONArray) dataArray.get(i);
                Date d = new Date(Long.parseLong(data.getStr(0)));

                CandleStickData csd = new CandleStickData();
                csd.setTimestamp(DateUtil.format(d, "yyyyMMddHHmm"));// 时间戳
                csd.setOpen(data.getStr(1));// 开盘价
                csd.setHigh(data.getStr(2));// 最高价
                csd.setLow(data.getStr(3));// 最低价
                csd.setClose(data.getStr(4));// 收盘价
                checkAndStore(pair, minute, csd);
            }
        }
        return "ok";

    }

    private void checkAndStore(String pair, String minute, CandleStickData csd) {
        String key = pair + "_" + minute;
        String keyData = pair + "_" + minute + "_data";
        String timestamp = csd.getTimestamp();
        String latestTimestamp = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(latestTimestamp)) {
            stringRedisTemplate.opsForZSet().add(keyData, JSONUtil.toJsonStr(csd), Double.parseDouble(timestamp));
            stringRedisTemplate.opsForValue().set(key, timestamp);
        } else {
            long t1 = Long.parseLong(timestamp);
            long t2 = Long.parseLong(latestTimestamp);
            if (t1 > t2) {
                stringRedisTemplate.opsForZSet().add(keyData, JSONUtil.toJsonStr(csd), Double.parseDouble(timestamp));
                stringRedisTemplate.opsForValue().set(key, timestamp);
            }
        }
    }
}
