package com.sental.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class Category {
    private Long id;
    private String name;
    private Long parentId;

}