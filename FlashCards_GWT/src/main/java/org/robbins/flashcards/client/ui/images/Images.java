package org.robbins.flashcards.client.ui.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * This {@link ClientBundle} is used for all the button icons. Using a bundle
 * allows all of these images to be packed into a single image, which saves a
 * lot of HTTP requests, drastically improving startup time.
 */
public interface Images extends ClientBundle {

  ImageResource bold();
  ImageResource createLink();
  ImageResource delete();
  ImageResource deleteDisabled();
  ImageResource edit();
  ImageResource hr();
  ImageResource indent();
  ImageResource insertImage();
  ImageResource italic();
  ImageResource justifyCenter();
  ImageResource justifyLeft();
  ImageResource justifyRight();
  ImageResource ol();
  ImageResource outdent();
  ImageResource removeFormat();
  ImageResource removeLink();
  ImageResource strikeThrough();
  ImageResource subscript();
  ImageResource superscript();
  ImageResource ul();
  ImageResource underline();
}
