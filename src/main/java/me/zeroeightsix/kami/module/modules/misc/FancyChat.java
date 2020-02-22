package me.zeroeightsix.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.network.play.client.CPacketChatMessage;

/**
 * The epic packet base by 086 on 8/04/2018. Created by Hamburger on 2/01/2020
 */
@Module.Info(name = "FancyChat", category = Module.Category.MISC, description = "Allows you to talk in different unicode types")
public class FancyChat extends Module {


	private Setting<ChatMode> mode = register(Settings.e("Mode", ChatMode.CIRCLED));



	public enum ChatMode{
		CIRCLED, FURRY, MIRRORED, ENCHANTED
	}

	@EventHandler
	public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
		if (event.getPacket() instanceof CPacketChatMessage) {
			String s = ((CPacketChatMessage) event.getPacket()).getMessage();
			if (s.startsWith("/")) {
			    return;
            }

		switch (mode.getValue()) {
			case CIRCLED:
				s = s.replace("a", "\u24D0");
				s = s.replace("b", "\u24D1");
				s = s.replace("c", "\u24D2");
				s = s.replace("d", "\u24D3");
				s = s.replace("e", "\u24D4");
				s = s.replace("f", "\u24D5");
				s = s.replace("g", "\u24D6");
				s = s.replace("h", "\u24D7");
				s = s.replace("i", "\u24D8");
				s = s.replace("j", "\u24D9");
				s = s.replace("k", "\u24DA");
				s = s.replace("l", "\u24DB");
				s = s.replace("m", "\u24DC");
				s = s.replace("n", "\u24DD");
				s = s.replace("o", "\u24DE");
				s = s.replace("p", "\u24DF");
				s = s.replace("q", "\u24E0");
				s = s.replace("r", "\u24E1");
				s = s.replace("s", "\u24E2");
				s = s.replace("t", "\u24E3");
				s = s.replace("u", "\u24E4");
				s = s.replace("v", "\u24E5");
				s = s.replace("w", "\u24E6");
				s = s.replace("x", "\u24E7");
				s = s.replace("y", "\u24E8");
				s = s.replace("z", "\u24E9");

				s = s.replace("0", "\u24EA");
				s = s.replace("1", "\u2460");
				s = s.replace("2", "\u2461");
				s = s.replace("3", "\u2462");
				s = s.replace("4", "\u2463");
				s = s.replace("5", "\u2464");
				s = s.replace("6", "\u2465");
				s = s.replace("7", "\u2466");
				s = s.replace("8", "\u2467");
				s = s.replace("9", "\u2468");

				s = s.replace("A", "\u24B6");
				s = s.replace("B", "\u24B7");
				s = s.replace("C", "\u24B8");
				s = s.replace("D", "\u24B9");
				s = s.replace("E", "\u24BA");
				s = s.replace("F", "\u24BB");
				s = s.replace("G", "\u24BC");
				s = s.replace("H", "\u24BD");
				s = s.replace("I", "\u24BE");
				s = s.replace("J", "\u24BF");
				s = s.replace("K", "\u24C0");
				s = s.replace("L", "\u24C1");
				s = s.replace("M", "\u24C2");
				s = s.replace("N", "\u24C3");
				s = s.replace("O", "\u24C4");
				s = s.replace("P", "\u24C5");
				s = s.replace("Q", "\u24C6");
				s = s.replace("R", "\u24C7");
				s = s.replace("S", "\u24C8");
				s = s.replace("T", "\u24C9");
				s = s.replace("U", "\u24CA");
				s = s.replace("V", "\u24CB");
				s = s.replace("W", "\u24CC");
				s = s.replace("X", "\u24CD");
				s = s.replace("Y", "\u24CE");
				s = s.replace("Z", "\u24CF");
				break;

				// This one makes me want to die
			case FURRY:
				s = s.replace("L", "w");
				s = s.replace("l", "w");
				s = s.replace("r", "w");
				s = s.replace("R", "w");
				s = s.replaceAll("shout","awoo");
				s = s.replaceAll("fuck","fluff");
				s = s.replaceAll("sex", "snuzzles");
				break;

			case MIRRORED:
				s = s.replace("0", "0");
				s = s.replace("1", "\u0196");
				s = s.replace("2", "\u1105");
				s = s.replace("3", "\u0190");
				s = s.replace("4", "\u3123");
				s = s.replace("5", "\u03DB");
				s = s.replace("6", "9");
				s = s.replace("7", "\u3125");
				s = s.replace("8", "8");
				s = s.replace("9", "6");

				s = s.replace("a", "\u0250");
				s = s.replace("b", "\u0071");
				s = s.replace("c", "\u0254");
				s = s.replace("d", "\u0070");
				s = s.replace("e", "\u01DD");
				s = s.replace("f", "\u025F");
				s = s.replace("g", "\u0183");
				s = s.replace("h", "\u0265");
				s = s.replace("i", "\u1D09");
				s = s.replace("j", "\u027E");
				s = s.replace("k", "\u029E");
				s = s.replace("l", "l");
				s = s.replace("m", "\u026F");
				s = s.replace("n", "u");
				s = s.replace("o", "o");
				s = s.replace("p", "d");
				s = s.replace("q", "b");
				s = s.replace("r", "\u0279");
				s = s.replace("s", "s");
				s = s.replace("t", "\u0287");
				s = s.replace("u", "n");
				s = s.replace("v", "\u028C");
				s = s.replace("w", "M");
				s = s.replace("x", "x");
				s = s.replace("y", "\u028E");
				s = s.replace("z", "z");

				s = s.replace("A", "\u2200");
				s = s.replace("B", "q");
				s = s.replace("C", "\u0186");
				s = s.replace("D", "p");
				s = s.replace("E", "\u018E");
				s = s.replace("F", "\u2132");
				s = s.replace("G", "\u05E4");
				s = s.replace("H", "H");
				s = s.replace("I", "I");
				s = s.replace("J", "\u017F");
				s = s.replace("K", "\u029E");
				s = s.replace("L", "\u02E5");
				s = s.replace("M", "\u0057");
				s = s.replace("N", "N");
				s = s.replace("O", "O");
				s = s.replace("P", "d");
				s = s.replace("Q", "Q");
				s = s.replace("R", "\u0279");
				s = s.replace("S", "S");
				s = s.replace("T", "\u2534");
				s = s.replace("U", "\u2229");
				s = s.replace("V", "\u039B");
				s = s.replace("W", "\u004D");
				s = s.replace("X", "X");
				s = s.replace("Y", "\u2144");
				s = s.replace("Z", "Z");

				s = new StringBuilder(s).reverse().toString();
				break;

			case ENCHANTED:
				s = s.replace("a", "\u1511");
				s = s.replace("b", "\u0296");
				s = s.replace("c", "\u14F5");
				s = s.replace("d", "\u21B8");
				s = s.replace("e", "\u14B7");
				s = s.replace("f", "\u2393");
				s = s.replace("g", "\u22A3");
				s = s.replace("h", "\u2351");
				s = s.replace("i", "\u254E");
				s = s.replace("j", "\u22EE");
				s = s.replace("k", "\uA58C");
				s = s.replace("l", "\uA58E");
				s = s.replace("m", "\u14B2");
				s = s.replace("n", "\u30EA");
				s = s.replace("o", "\u29A1");
				s = s.replace("p", "!\u00A1");
				s = s.replace("q", "\u1451");
				s = s.replace("r", "\u2237");
				s = s.replace("s", "\u14ED");
				s = s.replace("t", "\u2138");
				s = s.replace("u", "\u268D");
				s = s.replace("v", "\u234A");
				s = s.replace("w", "\u2234");
				s = s.replace("x", "\u2A18");
				s = s.replace("y", "||");
				s = s.replace("z", "\u2A05");

				s = s.replace("A", "\u1511");
				s = s.replace("B", "\u0296");
				s = s.replace("C", "\u14F5");
				s = s.replace("D", "\u21B8");
				s = s.replace("E", "\u14B7");
				s = s.replace("F", "\u2393");
				s = s.replace("G", "\u22A3");
				s = s.replace("H", "\u2351");
				s = s.replace("I", "\u254E");
				s = s.replace("J", "\u22EE");
				s = s.replace("K", "\uA58C");
				s = s.replace("L", "\uA58E");
				s = s.replace("M", "\u14B2");
				s = s.replace("N", "\u30EA");
				s = s.replace("O", "\u29A1");
				s = s.replace("P", "!\u00A1");
				s = s.replace("Q", "\u1451");
				s = s.replace("R", "\u2237");
				s = s.replace("S", "\u14ED");
				s = s.replace("T", "\u2138");
				s = s.replace("U", "\u268D");
				s = s.replace("V", "\u234A");
				s = s.replace("W", "\u2234");
				s = s.replace("X", "\u2A18");
				s = s.replace("Y", "||");
				s = s.replace("Z", "\u2A05");
				break;

		}


			if (s.length() >= 256) s = s.substring(0,256);
			((CPacketChatMessage) event.getPacket()).message = s;
		}
	});

}