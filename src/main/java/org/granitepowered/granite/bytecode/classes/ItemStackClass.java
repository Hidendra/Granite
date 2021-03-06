/*
 * License (MIT)
 *
 * Copyright (c) 2014-2015 Granite Team
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

package org.granitepowered.granite.bytecode.classes;

import static org.granitepowered.granite.util.MinecraftUtils.unwrap;
import static org.granitepowered.granite.util.MinecraftUtils.wrap;

import org.granitepowered.granite.Granite;
import org.granitepowered.granite.bytecode.BytecodeClass;
import org.granitepowered.granite.bytecode.Proxy;
import org.granitepowered.granite.bytecode.ProxyCallbackInfo;
import org.granitepowered.granite.impl.block.GraniteBlockState;
import org.granitepowered.granite.impl.entity.player.GraniteEntityPlayerMP;
import org.granitepowered.granite.impl.event.player.GranitePlayerPlaceBlockEvent;
import org.granitepowered.granite.impl.world.GraniteWorld;
import org.granitepowered.granite.mc.MCBlockPos;
import org.granitepowered.granite.mc.MCEntityPlayerMP;
import org.granitepowered.granite.mc.MCEnumFacing;
import org.granitepowered.granite.mc.MCItemStack;
import org.granitepowered.granite.mc.MCPacket;
import org.granitepowered.granite.mc.MCWorld;
import org.granitepowered.granite.util.Instantiator;
import org.spongepowered.api.block.BlockLoc;
import org.spongepowered.api.block.BlockSnapshot;

public class ItemStackClass extends BytecodeClass {

    public ItemStackClass() {
        super("ItemStack");
    }

    @Proxy(methodName = "onItemUse")
    public Object onItemUse(ProxyCallbackInfo<MCItemStack> info) throws Throwable {
        MCEntityPlayerMP mcPlayer = (MCEntityPlayerMP) info.getArguments()[0];
        MCWorld mcWorld = (MCWorld) info.getArguments()[1];
        MCBlockPos posClicked = (MCBlockPos) info.getArguments()[2];
        MCEnumFacing face = (MCEnumFacing) info.getArguments()[3];

        GraniteEntityPlayerMP player = wrap(mcPlayer);
        GraniteWorld world = wrap(mcWorld);

        BlockLoc clickedLoc = world.getBlock(posClicked.fieldGet$x(), posClicked.fieldGet$y(), posClicked.fieldGet$z());

        MCBlockPos posPlaced = posClicked;
        if (!unwrap((GraniteBlockState) clickedLoc.getState()).fieldGet$block().isReplaceable(mcWorld, posClicked)) {
            posPlaced = posClicked.offset(face, 1);
        }

        BlockLoc loc = world.getBlock(posPlaced.fieldGet$x(), posPlaced.fieldGet$y(), posPlaced.fieldGet$z());

        BlockSnapshot oldSnapshot = loc.getSnapshot();
        // TODO: Send PR to Sponge devs with hitX, hitY, hitZ (args[4 - 6])

        boolean ret = (boolean) info.callback();

        BlockSnapshot newSnapshot = loc.getSnapshot();

        if (!oldSnapshot.equals(newSnapshot)) {
            GranitePlayerPlaceBlockEvent event = new GranitePlayerPlaceBlockEvent(loc, player, oldSnapshot);
            Granite.getInstance().getEventManager().post(event);

            if (event.isCancelled()) {
                loc.replaceWith(oldSnapshot);

                MCPacket clickedUpdate = Instantiator.get().newPacketBlockChange(mcPlayer.fieldGet$worldObj(), posClicked);

                player.sendPacket(clickedUpdate);

                MCPacket placedUpdate = Instantiator.get().newPacketBlockChange(mcPlayer.fieldGet$worldObj(), posPlaced);

                player.sendPacket(placedUpdate);
            }
            return !event.isCancelled();
        } else {
            return ret;
        }
    }
}
