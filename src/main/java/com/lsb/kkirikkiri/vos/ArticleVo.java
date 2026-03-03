package com.lsb.kkirikkiri.vos;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ArticleVo extends ArticleEntity {
    private String nickname;
    private  String thumbnailPath;
}
