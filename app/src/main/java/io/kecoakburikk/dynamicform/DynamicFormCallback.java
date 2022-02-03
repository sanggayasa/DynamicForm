package io.kecoakburikk.dynamicform;

import java.util.List;

public interface DynamicFormCallback {
    void success(List<DynamicForms.FormData> formData);
}