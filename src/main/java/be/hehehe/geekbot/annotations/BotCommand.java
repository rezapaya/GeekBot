package be.hehehe.geekbot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;

/**
 * 
 * Annotate any class to indicate that it contains trigger commands.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ApplicationScoped
public @interface BotCommand {

}
