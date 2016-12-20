/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.eclipse.wst.json.core.internal.download;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.json.internal.JSONPlugin;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class HttpClientProvider {

	private static final String JSON_DOWNLOAD_FOLDER = ".jsonDownloadFolder"; //$NON-NLS-1$
	public static final String PROTOCOL_FILE = "file"; //$NON-NLS-1$
	public static final String PROTOCOL_PLATFORM = "platform"; //$NON-NLS-1$

	public static File getFile(URL url) throws IOException {
		if (url == null) {
			return null;
		}
		if (PROTOCOL_FILE.equals(url.getProtocol())
				|| PROTOCOL_PLATFORM.equalsIgnoreCase(url.getProtocol())) {
			File file;
			try {
				file = new File(new URI(url.toExternalForm()));
			} catch (Exception e) {
				file = new File(url.getFile());
			}
			if (!file.exists()) {
				return null;
			}
			return file;
		}
		File file = getCachedFile(url);
		long urlLastModified = getLastModified(url);
		if (file.exists()) {
			long lastModified = file.lastModified();
			if (urlLastModified > lastModified) {
				file = download(file, url);
				if (file != null) {
					file.setLastModified(urlLastModified);
				}
			}
		} else {
			file = download(file, url);
			if (file != null && urlLastModified > -1) {
				file.setLastModified(urlLastModified);
			}
		}
		return file;
	}

	private static File download(File file, URL url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		OutputStream out = null;
		file.getParentFile().mkdirs();
		try {
			HttpHost target = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
			Builder builder = RequestConfig.custom();
			HttpHost proxy = getProxy(target);
			if (proxy != null) {
				builder = builder.setProxy(proxy);
			}
			RequestConfig config = builder.build();
			HttpGet request = new HttpGet(url.toURI());
			request.setConfig(config);
			response = httpclient.execute(target, request);
			InputStream in = response.getEntity().getContent();
			out = new BufferedOutputStream(new FileOutputStream(file));
			copy(in, out);
			return file;
		} catch (Exception e) {
			logWarning(e);
			;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
				}
			}
			try {
				httpclient.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	private static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[8192];
		int n = 0;
		while ((n = in.read(buffer)) != -1) {
			out.write(buffer, 0, n);
		}
	}

	private static long getLastModified(URL url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpHost target = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
			Builder builder = RequestConfig.custom();
			HttpHost proxy = getProxy(target);
			if (proxy != null) {
				builder = builder.setProxy(proxy);
			}
			RequestConfig config = builder.build();
			HttpHead request = new HttpHead(url.toURI());
			request.setConfig(config);
			response = httpclient.execute(target, request);
			Header[] s = response.getHeaders("last-modified");
			if (s != null && s.length > 0) {
				String lastModified = s[0].getValue();
				return new Date(lastModified).getTime();
			}
		} catch (Exception e) {
			logWarning(e);
			return -1;
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
				}
			}
			try {
				httpclient.close();
			} catch (IOException e) {
			}
		}
		return -1;
	}

	private static HttpHost getProxy(HttpHost target) {
		final IProxyService proxyService = getProxyService();
		IProxyData[] select = null;
		try {
			select = proxyService.select(new URI(target.toURI()));
		} catch (URISyntaxException e) {
			logWarning(e);
			return null;
		}
		String type = target.getSchemeName();
		for (IProxyData proxyData : select) {
			if (proxyData.getType().equals(type)) {
				return new HttpHost(proxyData.getHost(), proxyData.getPort());
			}
		}
		return null;
	}

	private static void logWarning(Exception e) {
		IStatus status = new Status(IStatus.WARNING, JSONCorePlugin.PLUGIN_ID, e.getMessage(), e);
		JSONCorePlugin.getDefault().getLog().log(status);
	}

	public static IProxyService getProxyService() {
		BundleContext bc = JSONPlugin.getDefault().getBundle().getBundleContext();
		ServiceReference<?> serviceReference = bc.getServiceReference(IProxyService.class.getName());
		IProxyService service = (IProxyService) bc.getService(serviceReference);
		return service;
	}

	private static File getCachedFile(URL url) {
		IPath stateLocation = JSONCorePlugin.getDefault().getStateLocation();
		IPath downloadFolder = stateLocation.append(JSON_DOWNLOAD_FOLDER);
		String urlPath = url.getPath();
		IPath filePath = downloadFolder.append(urlPath);
		File file = filePath.toFile();
		return file;
	}

}
