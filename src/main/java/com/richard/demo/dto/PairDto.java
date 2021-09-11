package com.richard.demo.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

import lombok.Data;

/**
 * @author richard.xu03@sap.com
 * @version v 0.1 2021/9/10 3:58 PM richard.xu Exp $
 */
@Data
public class PairDto {
    List<ImmutablePair<String, String>> pairs = new ArrayList<>();
}
