package engine;

import org.rythmengine.Rythm;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.template.ITemplate;

import java.util.Map;

public class RythmEngineStatus {

    private boolean available;

    public RythmEngineStatus() {
        available = true;
    }

    public synchronized boolean isAvailable() {
        return available;
    }

    public synchronized String render(final Map<String, Object> params, final TemplateClass templateClass) {
        available = false;
        final ITemplate templateInstance = templateClass.asTemplate(Rythm.engine());
        templateInstance.__setRenderArgs(params);
        final String result = templateInstance.render();
        available = true;
        return result;
    }

}
