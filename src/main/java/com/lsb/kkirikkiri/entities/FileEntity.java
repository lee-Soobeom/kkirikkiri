package com.lsb.kkirikkiri.entities;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class FileEntity {
    private int id;
    private String userId;
    private int articleId;
    private String originalFilename;
    private String savedFilename;
    private String savedFilepath;
    private long size;
}
