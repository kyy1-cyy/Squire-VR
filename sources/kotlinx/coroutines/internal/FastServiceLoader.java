package kotlinx.coroutines.internal;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.exifinterface.media.ExifInterface;
import com.bumptech.glide.load.Key;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.HttpUrl;

/* compiled from: FastServiceLoader.kt */
@Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bÀ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J!\u0010\u0005\u001a\u0004\u0018\u00010\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\b2\u0006\u0010\t\u001a\u00020\u0004H\u0082\bJ1\u0010\n\u001a\u0002H\u000b\"\u0004\b\u0000\u0010\u000b2\u0006\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u0002H\u000b0\bH\u0002¢\u0006\u0002\u0010\u0010J*\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u000b0\u0012\"\u0004\b\u0000\u0010\u000b2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u0002H\u000b0\b2\u0006\u0010\r\u001a\u00020\u000eH\u0002J\u0013\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00060\u0012H\u0000¢\u0006\u0002\b\u0014J/\u0010\u0015\u001a\b\u0012\u0004\u0012\u0002H\u000b0\u0012\"\u0004\b\u0000\u0010\u000b2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u0002H\u000b0\b2\u0006\u0010\r\u001a\u00020\u000eH\u0000¢\u0006\u0002\b\u0016J\u0016\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00040\u00122\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0016\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00040\u00122\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J,\u0010\u001d\u001a\u0002H\u001e\"\u0004\b\u0000\u0010\u001e*\u00020\u001f2\u0012\u0010 \u001a\u000e\u0012\u0004\u0012\u00020\u001f\u0012\u0004\u0012\u0002H\u001e0!H\u0082\b¢\u0006\u0002\u0010\"R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000¨\u0006#"}, d2 = {"Lkotlinx/coroutines/internal/FastServiceLoader;", HttpUrl.FRAGMENT_ENCODE_SET, "()V", "PREFIX", HttpUrl.FRAGMENT_ENCODE_SET, "createInstanceOf", "Lkotlinx/coroutines/internal/MainDispatcherFactory;", "baseClass", "Ljava/lang/Class;", "serviceClass", "getProviderInstance", ExifInterface.LATITUDE_SOUTH, "name", "loader", "Ljava/lang/ClassLoader;", NotificationCompat.CATEGORY_SERVICE, "(Ljava/lang/String;Ljava/lang/ClassLoader;Ljava/lang/Class;)Ljava/lang/Object;", "load", HttpUrl.FRAGMENT_ENCODE_SET, "loadMainDispatcherFactory", "loadMainDispatcherFactory$kotlinx_coroutines_core", "loadProviders", "loadProviders$kotlinx_coroutines_core", "parse", "url", "Ljava/net/URL;", "parseFile", "r", "Ljava/io/BufferedReader;", "use", "R", "Ljava/util/jar/JarFile;", "block", "Lkotlin/Function1;", "(Ljava/util/jar/JarFile;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "kotlinx-coroutines-core"}, k = 1, mv = {1, 6, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class FastServiceLoader {
    public static final FastServiceLoader INSTANCE = new FastServiceLoader();
    private static final String PREFIX = "META-INF/services/";

    private FastServiceLoader() {
    }

    public final List<MainDispatcherFactory> loadMainDispatcherFactory$kotlinx_coroutines_core() {
        MainDispatcherFactory mainDispatcherFactory;
        if (!FastServiceLoaderKt.getANDROID_DETECTED()) {
            return load(MainDispatcherFactory.class, MainDispatcherFactory.class.getClassLoader());
        }
        try {
            ArrayList result = new ArrayList(2);
            MainDispatcherFactory mainDispatcherFactory2 = null;
            try {
                Class clz$iv = Class.forName("kotlinx.coroutines.android.AndroidDispatcherFactory", true, MainDispatcherFactory.class.getClassLoader());
                mainDispatcherFactory = (MainDispatcherFactory) MainDispatcherFactory.class.cast(clz$iv.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
            } catch (ClassNotFoundException e) {
                mainDispatcherFactory = null;
            }
            if (mainDispatcherFactory != null) {
                MainDispatcherFactory $this$loadMainDispatcherFactory_u24lambda_u2d0 = mainDispatcherFactory;
                result.add($this$loadMainDispatcherFactory_u24lambda_u2d0);
            }
            try {
                Class clz$iv2 = Class.forName("kotlinx.coroutines.test.internal.TestMainDispatcherFactory", true, MainDispatcherFactory.class.getClassLoader());
                mainDispatcherFactory2 = (MainDispatcherFactory) MainDispatcherFactory.class.cast(clz$iv2.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
            } catch (ClassNotFoundException e2) {
            }
            if (mainDispatcherFactory2 != null) {
                MainDispatcherFactory $this$loadMainDispatcherFactory_u24lambda_u2d1 = mainDispatcherFactory2;
                result.add($this$loadMainDispatcherFactory_u24lambda_u2d1);
            }
            return result;
        } catch (Throwable th) {
            return load(MainDispatcherFactory.class, MainDispatcherFactory.class.getClassLoader());
        }
    }

    private final MainDispatcherFactory createInstanceOf(Class<MainDispatcherFactory> baseClass, String serviceClass) throws ClassNotFoundException {
        try {
            Class clz = Class.forName(serviceClass, true, baseClass.getClassLoader());
            return baseClass.cast(clz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private final <S> List<S> load(Class<S> service, ClassLoader loader) {
        try {
            return loadProviders$kotlinx_coroutines_core(service, loader);
        } catch (Throwable th) {
            return CollectionsKt.toList(ServiceLoader.load(service, loader));
        }
    }

    public final <S> List<S> loadProviders$kotlinx_coroutines_core(Class<S> service, ClassLoader loader) throws IOException {
        String fullServiceName = Intrinsics.stringPlus(PREFIX, service.getName());
        Enumeration urls = loader.getResources(fullServiceName);
        Iterable list = Collections.list(urls);
        Intrinsics.checkNotNullExpressionValue(list, "list(this)");
        Iterable $this$flatMap$iv = (List) list;
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$flatMap$iv) {
            URL it = (URL) element$iv$iv;
            Iterable list$iv$iv = INSTANCE.parse(it);
            CollectionsKt.addAll(destination$iv$iv, list$iv$iv);
        }
        Iterable providers = CollectionsKt.toSet((List) destination$iv$iv);
        if (!(!((Collection) providers).isEmpty())) {
            throw new IllegalArgumentException("No providers were loaded with FastServiceLoader".toString());
        }
        Iterable $this$map$iv = providers;
        Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            String it2 = (String) item$iv$iv;
            destination$iv$iv2.add(INSTANCE.getProviderInstance(it2, loader, service));
        }
        return (List) destination$iv$iv2;
    }

    private final <S> S getProviderInstance(String name, ClassLoader loader, Class<S> service) throws ClassNotFoundException {
        Class clazz = Class.forName(name, false, loader);
        if (service.isAssignableFrom(clazz)) {
            return service.cast(clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
        }
        throw new IllegalArgumentException(("Expected service of class " + service + ", but found " + clazz).toString());
    }

    private final List<String> parse(URL url) {
        BufferedReader bufferedReader;
        String path = url.toString();
        if (StringsKt.startsWith$default(path, "jar", false, 2, (Object) null)) {
            String pathToJar = StringsKt.substringBefore$default(StringsKt.substringAfter$default(path, "jar:file:", (String) null, 2, (Object) null), '!', (String) null, 2, (Object) null);
            String entry = StringsKt.substringAfter$default(path, "!/", (String) null, 2, (Object) null);
            JarFile $this$use$iv = new JarFile(pathToJar, false);
            try {
                bufferedReader = new BufferedReader(new InputStreamReader($this$use$iv.getInputStream(new ZipEntry(entry)), Key.STRING_CHARSET_NAME));
                try {
                    BufferedReader r = bufferedReader;
                    List<String> file = INSTANCE.parseFile(r);
                    CloseableKt.closeFinally(bufferedReader, null);
                    $this$use$iv.close();
                    return file;
                } finally {
                }
            } catch (Throwable e$iv) {
                try {
                    throw e$iv;
                } catch (Throwable e$iv2) {
                    try {
                        $this$use$iv.close();
                        throw e$iv2;
                    } catch (Throwable closeException$iv) {
                        ExceptionsKt.addSuppressed(e$iv, closeException$iv);
                        throw e$iv;
                    }
                }
            }
        } else {
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            try {
                BufferedReader reader = bufferedReader;
                List<String> file2 = INSTANCE.parseFile(reader);
                CloseableKt.closeFinally(bufferedReader, null);
                return file2;
            } catch (Throwable th) {
                try {
                    throw th;
                } finally {
                }
            }
        }
    }

    private final <R> R use(JarFile $this$use, Function1<? super JarFile, ? extends R> function1) {
        try {
            R rInvoke = function1.invoke($this$use);
            InlineMarker.finallyStart(1);
            $this$use.close();
            InlineMarker.finallyEnd(1);
            return rInvoke;
        } catch (Throwable e) {
            try {
                throw e;
            } catch (Throwable e2) {
                InlineMarker.finallyStart(1);
                try {
                    $this$use.close();
                    InlineMarker.finallyEnd(1);
                    throw e2;
                } catch (Throwable closeException) {
                    ExceptionsKt.addSuppressed(e, closeException);
                    throw e;
                }
            }
        }
    }

    private final List<String> parseFile(BufferedReader r) throws IOException {
        CharSequence $this$all$iv;
        Set names = new LinkedHashSet();
        while (true) {
            String line = r.readLine();
            if (line == null) {
                return CollectionsKt.toList(names);
            }
            String serviceName = StringsKt.trim((CharSequence) StringsKt.substringBefore$default(line, "#", (String) null, 2, (Object) null)).toString();
            String $this$all$iv2 = serviceName;
            int i = 0;
            while (true) {
                if (i >= $this$all$iv2.length()) {
                    $this$all$iv = 1;
                    break;
                }
                char element$iv = $this$all$iv2.charAt(i);
                i++;
                char it = (element$iv == '.' || Character.isJavaIdentifierPart(element$iv)) ? (char) 1 : (char) 0;
                if (it == 0) {
                    $this$all$iv = null;
                    break;
                }
            }
            if ($this$all$iv == null) {
                throw new IllegalArgumentException(Intrinsics.stringPlus("Illegal service provider class name: ", serviceName).toString());
            }
            if (serviceName.length() > 0) {
                names.add(serviceName);
            }
        }
    }
}
