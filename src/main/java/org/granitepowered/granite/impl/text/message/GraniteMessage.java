/*
 * License (MIT)
 *
 * Copyright (c) 2014 Granite Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.granitepowered.granite.impl.text.message;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.ShiftClickAction;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.message.Message;
import org.spongepowered.api.text.message.MessageBuilder;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class GraniteMessage<T> implements Message<T> {
    ImmutableList<Message<?>> children;
    TextColor color;
    TextStyle style;
    Optional<ClickAction<?>> clickAction;
    Optional<HoverAction<?>> hoverAction;
    Optional<ShiftClickAction<?>> shiftClickAction;

    GraniteMessage() {
        children = ImmutableList.<Message<?>>builder().build();
        color = TextColors.RESET;
        style = TextStyles.RESET;
        clickAction = Optional.absent();
        hoverAction = Optional.absent();
        shiftClickAction = Optional.absent();
    }

    @Override
    public TextColor getColor() {
        return color;
    }

    @Override
    public TextStyle getStyle() {
        return style;
    }

    @Override
    public List<Message<?>> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public Optional<ClickAction<?>> getClickAction() {
        return clickAction;
    }

    @Override
    public Optional<HoverAction<?>> getHoverAction() {
        return hoverAction;
    }

    @Override
    public Optional<ShiftClickAction<?>> getShiftClickAction() {
        return shiftClickAction;
    }

    @Override
    public Iterator iterator() {
        // I have no idea what I'm doing
        return Iterators.concat(Iterators.singletonIterator(this), Iterables.concat(children).iterator());
    }

    public static class GraniteText extends GraniteMessage<String> implements Message.Text {
        String text;

        @Override
        public String getContent() {
            return text;
        }

        @Override
        public MessageBuilder<String> builder() {
            return new GraniteMessageBuilder.GraniteTextMessageBuilder(getContent(), children, color, style, clickAction, hoverAction, shiftClickAction);
        }
    }

    // TODO: The other three types (translation, score and selector)
}