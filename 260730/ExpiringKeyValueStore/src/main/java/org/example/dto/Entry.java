package org.example.dto;

import java.time.Instant;

public record Entry (String key,
                     String value,
                     Instant exprTime,
                     Boolean isForever){
}
