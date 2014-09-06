package edu.bath.sensorframework.client;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.xmlpull.v1.XmlPullParser;

/**
 * Parses an <b>item</b> element as is defined in both the
 * {@link PubSubNamespace#BASIC} and {@link PubSubNamespace#EVENT} namespaces.
 * To parse the item contents, it will use whatever
 * {@link PacketExtensionProvider} is registered in <b>smack.providers</b> for
 * its element name and namespace. If no provider is registered, it will return
 * a {@link SimplePayload}.
 * 
 * @author Robin Collier
 */
public class ItemProvider implements PacketExtensionProvider {
        public PacketExtension parseExtension(XmlPullParser parser)
                        throws Exception {
                String id = parser.getAttributeValue(null, "id");
                String elem = parser.getName();

                int tag = parser.next();

                if (tag == XmlPullParser.END_TAG) {
                        return new Item(id);
                } else {
                        String payloadElemName = parser.getName();
                        String payloadNS = parser.getNamespace();

                        if (ProviderManager.getInstance().getExtensionProvider(
                                        payloadElemName, payloadNS) == null) {
                                StringBuilder payloadText = new StringBuilder();
                                boolean degenerated = false;

                                while (true) {
                                        if (tag == XmlPullParser.END_TAG
                                                        && parser.getName().equals(elem))
                                                break;

                                        if (parser.getEventType() == XmlPullParser.START_TAG) {
                                                payloadText.append("<").append(parser.getName());
                                                int n = parser.getAttributeCount();
                                                for (int i = 0; i < n; i++) {
                                                        payloadText.append(" ").append(
                                                                        parser.getAttributeName(i)).append("=\"")
                                                                        .append(parser.getAttributeValue(i))
                                                                        .append("\"");
                                                }
                                                if (parser.isEmptyElementTag()) {
                                                        payloadText.append("/>");
                                                        degenerated = true;
                                                } else
                                                        payloadText.append(">");
                                        } else if (parser.getEventType() == XmlPullParser.END_TAG) {
                                                if (degenerated)
                                                        degenerated = false;
                                                else
                                                        payloadText.append("</").append(parser.getName())
                                                                        .append(">");
                                        } else if (parser.getEventType() == XmlPullParser.TEXT) {
                                                payloadText.append(parser.getText());
                                        }

                                        tag = parser.next();
                                }
                                return new PayloadItem<SimplePayload>(id, new SimplePayload(
                                                payloadElemName, payloadNS, payloadText.toString()));
                        } else {
                                return new PayloadItem<PacketExtension>(id, PacketParserUtils
                                                .parsePacketExtension(payloadElemName, payloadNS,
                                                                parser));
                        }
                }
        }

}