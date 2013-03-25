package org.robbins.flashcards.webservices.base;

import java.util.List;

import javax.ws.rs.core.Response;

public interface GenericResource <T, Serializable> {

	public List<T> list(Integer page,
						Integer size,
						String sort,
						String direction);
	public Long count();
	public T findOne(Long id);
	public T post(T entity);
	public Response put(Long id, T entity);
	public Response delete(Long id);
	public Response update(Long id, T updatedEntity);
}