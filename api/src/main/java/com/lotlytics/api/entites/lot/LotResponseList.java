package com.lotlytics.api.entites.lot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.ArrayList;

@AllArgsConstructor
@Getter
public class LotResponseList {
    private ArrayList<LotResponse> lots;
}
