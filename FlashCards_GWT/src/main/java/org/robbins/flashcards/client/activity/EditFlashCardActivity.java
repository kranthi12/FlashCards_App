package org.robbins.flashcards.client.activity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.robbins.flashcards.client.factory.ClientFactory;
import org.robbins.flashcards.client.place.EditFlashCardPlace;
import org.robbins.flashcards.client.place.ListFlashCardsPlace;
import org.robbins.flashcards.client.place.NewFlashCardPlace;
import org.robbins.flashcards.client.ui.EditFlashCardView;
import org.robbins.flashcards.model.FlashCardDto;
import org.robbins.flashcards.model.TagDto;
import org.robbins.flashcards.service.FlashCardRestService;
import org.robbins.flashcards.service.TagRestService;
import org.robbins.flashcards.util.ConstsUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class EditFlashCardActivity extends AppAbstractActivity {

	private FlashCardDto flashCard;

	private final FlashCardRestService flashCardService;
	private final TagRestService tagService;
	private final EditFlashCardView display;

	public EditFlashCardActivity(ClientFactory clientFactory) {
		super(clientFactory);
		GWT.log("Creating 'EditFlashCardActivity'");
		
		this.flashCardService = clientFactory.getFlashCardService();
		this.tagService = clientFactory.getTagService();
		this.display = clientFactory.getEditFlashCardView();
		
		((RestServiceProxy)flashCardService).setResource(new Resource(""));
		((RestServiceProxy)tagService).setResource(new Resource(""));
		
		EditFlashCardActivity.this.display.initFormValidation();
//		if (this.display instanceof RequiresLogin) {
//			requireLogin(this.display);
//		}
		bind();
		
		String fields = "id,name";
		tagService.getTags(ConstsUtil.DEFAULT_AUTH_HEADER, fields, new MethodCallback<List<TagDto>>() {
			public void onFailure(Method method, Throwable caught) {
				GWT.log("EditFlashCardActivity: Error loading Tags");
				Window.alert(getConstants().errorLoadingTags());
			}
			public void onSuccess(Method method, List<TagDto> result) {
				GWT.log("TagsActivity: Loading Tag list: " + result.size() + " tags");
				EditFlashCardActivity.this.display.setTagsData(result);
			}
		});
	}
	
	public EditFlashCardActivity(NewFlashCardPlace place, ClientFactory clientFactory) {
		this(clientFactory);
		setFlashCard(new FlashCardDto());
		
		EditFlashCardActivity.this.display.setFlashCardData(null);
	}

	public EditFlashCardActivity(EditFlashCardPlace place, ClientFactory clientFactory) {
		this(clientFactory);
		
		Long id = Long.parseLong(place.getPlaceName());
		
		String fields = ConstsUtil.DEFAULT_FLASHCARDS_FIELDS;
		flashCardService.getFlashCard(ConstsUtil.DEFAULT_AUTH_HEADER, id, fields, new MethodCallback<FlashCardDto>() {
			public void onFailure(Method method, Throwable caught) {
				GWT.log("EditFlashCardActivity: Error loading data");
				Window.alert(getConstants().errorLoadingTag());
			}
			public void onSuccess(Method method, FlashCardDto flashCard) {
				GWT.log("EditFlashCardActivity: 'Load FlashCard' FlashCardId: " + flashCard.getId());
				EditFlashCardActivity.this.setFlashCard(flashCard);
				EditFlashCardActivity.this.display.setFlashCardData(flashCard);
			}
		});
	}
	
	public void bind() {
		getRegistrations().add(display.getSaveButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				GWT.log("EditFlashCardActivity: 'Submit' button clicked");
				if (!display.validate()) return;
				EditFlashCardActivity.this.display.getSubmitEnabled().setEnabled(false);
				doSave();
			}
		}));

		getRegistrations().add(display.getCancelButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				GWT.log("EditFlashCardActivity: 'Cancel' button clicked");
				History.back();
			}
		}));
	}

	public FlashCardDto getFlashCard() {
		return flashCard;
	}

	public void setFlashCard(FlashCardDto flashCard) {
		this.flashCard = flashCard;
	}
	
	@Override
	public void start(AcceptsOneWidget container, EventBus eventBus) {
		container.setWidget(display.asWidget());
	}

	private void doSave() {
		
		getFlashCard().setQuestion(display.getQuestion().getText());
		getFlashCard().setAnswer(display.getAnswer().getHTML());
		getFlashCard().setLinks(display.getLinks());
		getFlashCard().setTags(getTags(display.getTags()));

		// is this an existing FlashCard?
		if ( (getFlashCard().getId() != null) && (getFlashCard().getId() != 0)) {
			updateFlashCard(getFlashCard());
		} else {
			saveFlashCard(getFlashCard());			
		}
	}

	private void saveFlashCard(FlashCardDto flashcard) {
		flashCardService.postFlashCards(ConstsUtil.DEFAULT_AUTH_HEADER, flashcard, new MethodCallback<FlashCardDto>() {
			public void onFailure(Method method, Throwable caught) {
				GWT.log("EditFlashCardActivity: Error saving data");
				Window.alert(getConstants().errorSavingTag());
				EditFlashCardActivity.this.display.getSubmitEnabled().setEnabled(true);
			}
			public void onSuccess(Method method, FlashCardDto result) {
				GWT.log("EditFlashCardActivity: FlashCard Saved:" + result);
				EditFlashCardActivity.this.display.getSubmitEnabled().setEnabled(true);
				getPlaceController().goTo(new ListFlashCardsPlace(""));
			}
		});		
	}
	
	private void updateFlashCard(FlashCardDto flashcard) {
		flashCardService.putFlashCard(ConstsUtil.DEFAULT_AUTH_HEADER, flashcard.getId(), flashcard, new MethodCallback<java.lang.Void>() {
			public void onFailure(Method method, Throwable caught) {
				GWT.log("EditFlashCardActivity: Error saving data");
				Window.alert(getConstants().errorSavingTag());
				EditFlashCardActivity.this.display.getSubmitEnabled().setEnabled(true);
			}
			public void onSuccess(Method method, java.lang.Void result) {
				GWT.log("EditFlashCardActivity: FlashCard updated");
				EditFlashCardActivity.this.display.getSubmitEnabled().setEnabled(true);
				getPlaceController().goTo(new ListFlashCardsPlace(""));
			}
		});
	}
	
	private Set<TagDto> getTags(List<String> tagsList) {
		Set<TagDto> tags = new HashSet<TagDto>();

		if (tagsList.size() > 0) {
			for (String tagName : tagsList) {
				TagDto tag = new TagDto(tagName);
				tags.add(tag);
			}
		}
		return tags;
	}
}
