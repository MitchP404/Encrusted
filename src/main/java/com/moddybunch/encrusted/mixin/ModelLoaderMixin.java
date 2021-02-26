package com.moddybunch.encrusted.mixin;

import com.moddybunch.encrusted.Encrusted;
import com.moddybunch.encrusted.api.JsonGen;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelLoader.class)
public class ModelLoaderMixin {

    @Inject(method = "loadModelFromJson", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Lnet/minecraft/resource/Resource;"), cancellable = true)
    public void loadModelFromJson(Identifier id, CallbackInfoReturnable<JsonUnbakedModel> cir) {
        //First, we check if the current item model that is being registered is from our mod. If it isn't, we continue.
        if (!Encrusted.MODID.equals(id.getNamespace())) return;

        String baseItem = id.getPath();

        //Check if the item is an encrusted item (if not then continue)
        if(!baseItem.contains("encrusted")) return;
        //Pull the encrustor out
        String encrustor = baseItem.substring(5, baseItem.indexOf("encrusted") - 1);
        baseItem = baseItem.substring(baseItem.indexOf("encrusted") + 10);

        //Get the armor part
        String armor = baseItem.substring(baseItem.indexOf("_") + 1);

        String modelJson = JsonGen.createItemModelJson(baseItem, armor + "_" + encrustor,"generated");
        if ("".equals(modelJson)) return;
        //We check if the json string we get is valid before continuing.
        JsonUnbakedModel model = JsonUnbakedModel.deserialize(modelJson);
        model.id = id.toString();
        cir.setReturnValue(model);
        cir.cancel();
    }
}
