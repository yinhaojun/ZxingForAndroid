package com.yinjunzi.zxinglibrary.embedded.barcodescanner.camera;

import com.yinjunzi.zxinglibrary.embedded.barcodescanner.SourceData;

/**
 * Callback for camera previews.
 */
public interface PreviewCallback {
    void onPreview(SourceData sourceData);
    void onPreviewError(Exception e);
}
