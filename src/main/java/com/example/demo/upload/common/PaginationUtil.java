package com.example.demo.upload.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling pagination.
 *
 * <p>
 * Pagination uses the same principles as the
 * <a href="https://developer.github.com/v3/#pagination">Github API</a>, and
 * follow <a href="http://tools.ietf.org/html/rfc5988">RFC 5988 (Link
 * header)</a>.
 */
public final class PaginationUtil {

	private PaginationUtil() {
	}

	public static HttpHeaders generatePaginationHttpHeaders(Page<?> page, String baseUrl) throws URISyntaxException {

		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Total-Count", "" + Long.toString(page.getTotalElements()));
		String link = "";
		if ((page.getNumber() + 1) < page.getTotalPages()) {
			link = "<" + generateUri(baseUrl, page.getNumber() + 1, page.getSize()) + ">; rel=\"next\",";
		}
		// prev link
		if ((page.getNumber()) > 0) {
			link += "<" + generateUri(baseUrl, page.getNumber() - 1, page.getSize()) + ">; rel=\"prev\",";
		}
		// last and first link
		int lastPage = 0;
		if (page.getTotalPages() > 0) {
			lastPage = page.getTotalPages() - 1;
		}
		link += "<" + generateUri(baseUrl, lastPage, page.getSize()) + ">; rel=\"last\",";
		link += "<" + generateUri(baseUrl, 0, page.getSize()) + ">; rel=\"first\"";
		headers.add(HttpHeaders.LINK, link);
		return headers;
	}

	public static Page<?> convertListToPage(List<?> list, Pageable pageable) {
		int total = list.size();
		int size = pageable.getPageSize();
		int start = (int) pageable.getOffset();
		int end = (start + size) > total ? total : (start + size);
		if (start > end) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}
		return new PageImpl<>(list.subList(start, end), pageable, total);
	}

	private static String generateUri(String baseUrl, int page, int size) throws URISyntaxException {
		return UriComponentsBuilder.fromUriString(baseUrl).queryParam("page", page).queryParam("size", size)
				.toUriString();
	}
}
