package be.hehehe.geekbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.RandomAction;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.persistence.dao.ConnerieDAO;
import be.hehehe.geekbot.persistence.lucene.ConnerieIndex;
import be.hehehe.geekbot.persistence.model.Connerie;
import be.hehehe.geekbot.utils.BotUtils;
import be.hehehe.geekbot.utils.BundleUtil;

@BotCommand
public class ConnerieCommand {

	private static final List<String> lastSentences = new ArrayList<String>();
	private static final int MAXSENTENCES = 3;

	@Trigger(type = TriggerType.EVERYTHING)
	public List<String> storeEveryLines(String message, String author,
			boolean nickInMessage) {

		List<String> result = new ArrayList<String>();
		String url = BotUtils.extractURL(message);
		if (url == null) {
			pushSentence(message);
			if (!nickInMessage && message.length() > 9
					&& !message.contains("<") && !message.contains(">")
					&& !message.startsWith("!")) {
				ConnerieDAO dao = new ConnerieDAO();
				Connerie connerie = new Connerie(message);
				dao.save(connerie);
			}
		}

		return result;
	}

	private void pushSentence(String message) {
		lastSentences.add(0, message);
		if (lastSentences.size() > MAXSENTENCES) {
			lastSentences.remove(MAXSENTENCES);
		}
	}

	@RandomAction(3)
	@Trigger(type = TriggerType.BOTNAME)
	public String getRandomLine(String message) {
		String botName = BundleUtil.getBotName();
		message = message.replace(botName, "");
		message = message.replace("?", "");
		List<String> list = ConnerieIndex.findRelated(message, lastSentences);
		Random random = new Random();
		int irand = random.nextInt(list.size());
		String msg = list.get(irand);
		return msg;
	}

}