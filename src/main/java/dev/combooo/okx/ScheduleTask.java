package dev.combooo.okx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduleTask {

    @Autowired
    private OkxService okxService;

    // 每分钟获取一次K线数据
    @Scheduled(cron = "5 */1 * * * ?")
    public void getCandleStickData() {
        try {
            okxService.getCandleStickData("BTC-USDT-SWAP", "15m", "1");
        } catch (Exception e) {
            log.error("执行getCandleStickData异常！", e);
        }
    }
}
