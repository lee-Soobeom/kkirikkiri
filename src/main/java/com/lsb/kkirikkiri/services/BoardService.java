package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.BoardEntity;
import com.lsb.kkirikkiri.mappers.BoardMapper;
import com.lsb.kkirikkiri.validators.BoardValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;

    public BoardEntity getBoardById (String id) {
        if (!BoardValidator.validateId(id)) {
            return null;
        }
        return this.boardMapper.selectById(id);
    }
}
