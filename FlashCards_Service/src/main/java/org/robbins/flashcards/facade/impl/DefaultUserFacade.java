package org.robbins.flashcards.facade.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.robbins.flashcards.dto.UserDto;
import org.robbins.flashcards.facade.UserFacade;
import org.robbins.flashcards.facade.base.AbstractCrudFacadeImpl;
import org.robbins.flashcards.model.User;
import org.robbins.flashcards.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserFacade extends AbstractCrudFacadeImpl<UserDto, User> implements UserFacade {
	
	@Inject
	private UserService service;

	public UserService getService() {
		return service;
	}
	
	public UserDto findUserByOpenid(String openid) {
		User result = service.findUserByOpenid(openid);
		UserDto userDto = getMapper().map(result, UserDto.class);
		return userDto;
	}

	@Override
	public UserDto save(UserDto dto) {
		User entity = getEntity(dto);
		
		if (!dto.isNew()) {
			User orig = service.findOne(dto.getId());
			entity.setCreatedBy(orig.getCreatedBy());
			entity.setCreatedDate(orig.getCreatedDate());
		}
		
		User resultEntity = getService().save(entity);
		UserDto resultDto = getDto(resultEntity);
		return resultDto;
	}
	
	@Override
	public UserDto getDto(User entity) {
		return getMapper().map(entity, UserDto.class);
	}

	@Override
	public User getEntity(UserDto dto) {
		return getMapper().map(dto, User.class);
	}
	
	@Override
	public List<UserDto> getDtos(List<User> entities) {
		List<UserDto> dtos = new ArrayList<UserDto>();
		for (User entity : entities){
			dtos.add(getDto(entity));
		}
		return dtos;
	}

	@Override
	public List<User> getEtnties(List<UserDto> dtos) {
		List<User> entities = new ArrayList<User>();
		for (UserDto dto : dtos){
			entities.add(getEntity(dto));
		}
		return entities;
	}
}
