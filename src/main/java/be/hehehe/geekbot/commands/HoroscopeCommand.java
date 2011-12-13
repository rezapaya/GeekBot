package be.hehehe.geekbot.commands;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.utils.IRCUtils;
import be.hehehe.geekbot.utils.LOG;

@BotCommand
public class HoroscopeCommand {

	private static final Map<String, String> mapping = new HashMap<String, String>();

	static {
		mapping.put("belier", "0");
		mapping.put("taureau", "1");
		mapping.put("gemeaux", "2");
		mapping.put("cancer", "3");
		mapping.put("lion", "4");
		mapping.put("vierge", "5");
		mapping.put("balance", "6");
		mapping.put("scorpion", "7");
		mapping.put("sagittaire", "8");
		mapping.put("capricorne", "9");
		mapping.put("verseau", "10");
		mapping.put("poissons", "11");

	}

	@Trigger(value = "!horoscope", type = TriggerType.EXACTMATCH)
	public String getHoroscopeHelp() {
		return IRCUtils.bold("!horoscope <signe>")
				+ " - Available signs : belier, taureau, gemeaux, cancer, lion, vierge, balance, scorpion, sagittaire, capricorne, verseau, poisson";
	}

	@Trigger(value = "!horoscope", type = TriggerType.STARTSWITH)
	public String getHoroscope(String sign) {
		if ("poisson".equals(sign)) {
			sign = "poissons";
		}

		String result = "";
		try {
			Document doc = Jsoup.parse(new URL(
					"http://www.astrocenter.fr/fr/FCDefault.aspx?Af=0"), 10000);
			Element horo = doc.select("div#ast-sign-" + mapping.get(sign))
					.first();
			horo = horo.select(".ast-description p").first();
			result = horo.text();
			result = result.replace('�', '\'');
			result = result.replaceAll("�", "...");
		} catch (Exception e) {
			LOG.handle(e);
		}

		return result;
	}
}