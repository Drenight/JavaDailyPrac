package org.example.dto;

public record ConfigVersion (
        String key,
        String value,
        long version
){
}
