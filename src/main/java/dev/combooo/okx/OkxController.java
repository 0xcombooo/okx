package dev.combooo.okx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController("/okx")
public class OkxController {

    @Autowired
    private OkxService okxService;


    @GetMapping("/getCandleStickData/{pair}/{minute}/{limit}")
    public String getCandleStickData(@PathVariable String pair, @PathVariable String minute, @PathVariable String limit) {

        return okxService.getCandleStickData(pair, minute, limit);
    }
}
