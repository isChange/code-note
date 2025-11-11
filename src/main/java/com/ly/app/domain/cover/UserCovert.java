package com.ly.app.domain.cover;

import com.ly.app.domain.entity.User;
import com.ly.app.domain.vo.user.SafeUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/6/1 21:19
 * @email liuyia2022@163.com
 */
@Mapper
public interface UserCovert {
    UserCovert INSTANCE = Mappers.getMapper(UserCovert.class);
    SafeUserVO userToSafeUserVO(User user);
}
