const KAFKA_UTILS_BACKEND = '/kafka-utils/api/*';

const defaultProxy = () => ({
  [KAFKA_UTILS_BACKEND]: {
    target: 'http://localhost:8080',
    secure: false,
    pathRewrite: {
      '^/kafka-utils': '',
    },
  }
});

const debug = (logLevel = 'debug') => {
  const proxy = defaultProxy();

  for (const proxyConfig of Object.values(proxy)) {
    proxyConfig.logLevel = logLevel;
  }

  return proxy;
};

const proxyConfig = {
  debug,
  KAFKA_UTILS_BACKEND,
  default: defaultProxy,
};

const proxy = proxyConfig.debug();

module.exports = proxy;


