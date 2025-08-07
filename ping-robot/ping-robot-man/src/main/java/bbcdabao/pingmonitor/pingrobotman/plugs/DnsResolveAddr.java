package bbcdabao.pingmonitor.pingrobotman.plugs;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

import bbcdabao.pingmonitor.common.domain.extraction.annotation.ExtractionFieldMark;
import bbcdabao.pingmonitor.pingrobotapi.IPingMoniterPlug;

public class DnsResolveAddr implements IPingMoniterPlug {

    private final Logger logger = LoggerFactory.getLogger(DnsResolveAddr.class);

    @ExtractionFieldMark(descriptionCn = "域名", descriptionEn = "Domain Name")
    private String domainName;

    @Override
    public String doPingExecute(int timeOutMs) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("dns:");
        sb.append(domainName);

        try {
            Lookup lookup = new Lookup(domainName, Type.SOA);
            // 使用阿里 DNS，更适合在中国运行
            Resolver resolver = new SimpleResolver("223.5.5.5");
            resolver.setTimeout(Duration.ofMillis(timeOutMs / 2));
            lookup.setResolver(resolver);
            lookup.setCache(null);

            Record[] records = lookup.run();
            if (lookup.getResult() != Lookup.SUCCESSFUL) {
                logger.warn("DnsResolveAddr: [{}] DNS lookup failed: {}", domainName, lookup.getErrorString());
                throw new Exception("Domain may not be valid: no SOA record.");
            }

            sb.append(":ok");
            for (Record record : records) {
                sb.append(":").append(record.rdataToString());
            }

            logger.info("DnsResolveAddr: [{}] resolved successfully", domainName);
            return sb.toString();
        } catch (Exception e) {
            logger.warn("DnsResolveAddr: [{}] resolution error", domainName, e);
            throw new Exception("DNS resolution failed for domain: " + domainName, e);
        }
    }
}
