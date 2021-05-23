import { Injectable } from '@angular/core';
import {Configuration} from "../../../../generated-sources/kafka-utils-client";
import {AuthenticationService} from "../../authentication/authentication.service";

export class NotLoggedError implements Error {
  message: string;
  name: string;
  constructor() {
    this.name = 'NotLoggedError'
    this.message = 'There is no active login!';
  }
}

@Injectable()
export class KafkaUtilsConfigurationService extends Configuration {

	constructor(
		private readonly _authenticationService: AuthenticationService,
	) {
		super({
			basePath   : '/kafka-utils/api',
			accessToken: () => this._getAccessToken(),
		});
	}

	private _getAccessToken(): string {
		const token = this._authenticationService.getToken();

		if (!token) {
			throw new NotLoggedError();
		}

		return token;
	}

}
