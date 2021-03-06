package org.robbins.flashcards.facade.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.robbins.flashcards.exceptions.ServiceException;
import org.robbins.flashcards.service.util.FieldInitializerUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public abstract class AbstractCrudFacadeImpl<D, E> implements
		GenericCrudFacade<D>, CrudFacade<D, E> {

	@Inject
	private Mapper mapper;

	@Inject
	private FieldInitializerUtil fieldInitializer;
	
	public Mapper getMapper() {
		return mapper;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public D save(D dto) {
		E entity = getEntity(dto);
		E resultEntity = getService().save(entity);
		D resultDto = getDto(resultEntity);
		return resultDto;
	}

	@Override
	public List<D> list(Integer page, Integer size, String sort,
			String direction) throws ServiceException {
		return this.list(page, size, sort, direction, null);
	}
	
	@Override
	public List<D> list(Integer page, Integer size, String sort,
			String direction, Set<String> fields) throws ServiceException {

		List<E> entities = null;

		// are we trying to use Pagination or Sorting?
		// if not then go ahead and return findAll()
		if ((page == null) && (StringUtils.isEmpty(sort))) {
			entities = getService().findAll();
		}
		// should we Page
		else if (page != null) {
			PageRequest pageRequest = getPageRequest(page, size, sort,
					direction);
			entities = getService().findAll(pageRequest);
		}
		// should we just Sort the list?
		else if (!StringUtils.isEmpty(sort)) {
			// get a sorted list
			Sort entitySort = getSort(sort, direction);
			entities = getService().findAll(entitySort);
		}

		if (entities == null) {
			return null;
		}

		if (CollectionUtils.isNotEmpty(fields)) {
			fieldInitializer.initializeEntity(entities, fields);
		}
		
		List<D> dtos = new ArrayList<D>();
		for (E entity : entities) {
			dtos.add(getDto(entity));
		}

		return dtos;
	}

	@Override
	public Long count() {
		return getService().count();
	}

	@Override
	public D findOne(Long id) throws ServiceException {
		return findOne(id, null);
	}

	@Override
	public D findOne(Long id, Set<String> fields) throws ServiceException {
		E resultEntity = getService().findOne(id);

		if (CollectionUtils.isNotEmpty(fields)) {
			fieldInitializer.initializeEntity(resultEntity, fields);
		}
		
		return getDto(resultEntity);
	}
	
	@Override
	public void delete(Long id) {
		getService().delete(id);
	}

	protected PageRequest getPageRequest(Integer page, Integer size,
			String sortOrder, String sortDirection) {
		// are we Sorting too?
		if (!StringUtils.isEmpty(sortOrder)) {
			Sort sort = getSort(sortOrder, sortDirection);
			return new PageRequest(page, size, sort);
		} else {
			return new PageRequest(page, size);
		}
	}

	protected Sort getSort(String sort, String order) {
		if ((StringUtils.isEmpty(order)) || (order.equals("asc"))) {
			return new Sort(Direction.ASC, order);
		} else if (order.equals("desc")) {
			return new Sort(Direction.DESC, order);
		} else {
			// throw new GenericWebServiceException(Response.Status.BAD_REQUEST,
			// "Sort order must be 'asc' or 'desc'.  '" + order +
			// "' is not an acceptable sort order");

			throw new IllegalArgumentException(
					"Sort order must be 'asc' or 'desc'.  '" + order
							+ "' is not an acceptable sort order");
		}
	}
}
