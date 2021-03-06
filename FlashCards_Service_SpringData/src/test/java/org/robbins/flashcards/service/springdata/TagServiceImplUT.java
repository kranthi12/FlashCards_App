package org.robbins.flashcards.service.springdata;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robbins.flashcards.model.Tag;
import org.robbins.flashcards.repository.springdata.TagRepository;
import org.robbins.tests.BaseMockingTest;
import org.robbins.tests.UnitTest;
import org.springframework.test.util.ReflectionTestUtils;

@Category(UnitTest.class)
public class TagServiceImplUT extends BaseMockingTest {

	@Mock TagRepository repository;
	TagServiceImpl tagService;
	
	@Before
	public void before() {
		tagService = new TagServiceImpl();
		ReflectionTestUtils.setField(tagService, "repository", repository);		
	}

	@Test
	public void findByName() {
		
		when(repository.findByName(anyString())).thenReturn(new Tag("EJB"));
		
		Tag tag = tagService.findByName("EJB");
		
		verify(repository, Mockito.times(1)).findByName("EJB");
		assertThat(tag, is(Tag.class));
	}
}