package za.co.entelect.web.controllers.web;

import lombok.extern.slf4j.Slf4j;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;

@ControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalXssFilterControllerAdvice {

    private static PolicyFactory sanitizer = Sanitizers.FORMATTING
        .and(Sanitizers.BLOCKS)
        .and(Sanitizers.IMAGES)
        .and(Sanitizers.LINKS)
        .and(Sanitizers.STYLES);


    static class SanitizeInputsEditor extends PropertyEditorSupport {

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            String out = "";
            if (text != null) {
                out = sanitizer.sanitize(text);
            }

            setValue(out);
        }
    }

    static class SanitizeAndRestoreExcludedCharactersEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            String out = "";
            if (text != null) {
                out = sanitizer.sanitize(text)
                    .replace("&#64;", "@")
                    .replace("&#34;", "\"")
                    .replace("&#39;", "'")
                    .replace("&#43;", "+")
                    .replace("&#61;", "=")
                    .replace("&#96;", "`")
                    .replace("&amp;", "&")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">");
            }

            setValue(out);
        }
    }

    /**
     * Administrators need to be able to embed video content in static content blocks - this allows for that.
     */
    static class ExcludeFromSanitizeInputsEditor extends PropertyEditorSupport {

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue(text);
        }
    }

    private static SanitizeInputsEditor sanitizeInputsEditor = new SanitizeInputsEditor();

    private static SanitizeAndRestoreExcludedCharactersEditor sanitizeAndRestoreExcludedCharactersEditor =
        new SanitizeAndRestoreExcludedCharactersEditor();

    private static ExcludeFromSanitizeInputsEditor excludeFromSanitizeInputsEditor =
        new ExcludeFromSanitizeInputsEditor();

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, sanitizeInputsEditor);
        binder.registerCustomEditor(String.class, "email", sanitizeAndRestoreExcludedCharactersEditor);
        binder.registerCustomEditor(String.class, "changeEmail", sanitizeAndRestoreExcludedCharactersEditor);
        binder.registerCustomEditor(String.class, "password", sanitizeAndRestoreExcludedCharactersEditor);
        binder.registerCustomEditor(String.class, "passwordConfirmation", sanitizeAndRestoreExcludedCharactersEditor);
        binder.registerCustomEditor(String.class, "adminHtmlContent", excludeFromSanitizeInputsEditor);

    }
}
