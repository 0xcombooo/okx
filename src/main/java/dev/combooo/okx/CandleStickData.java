package dev.combooo.okx;

import lombok.Data;

@Data
public class CandleStickData {

    private String timestamp;

    private String open;

    private String high;

    private String low;

    private String close;

    private String volume;

    private String volumeCcy;
}
