import { ModuleWithProviders, NgModule } from '@angular/core';
import {KafkaUtilsConfigurationService} from "./kafka-utils-configuration.service";
import {ApiModule, Configuration} from "kafkaUtilsRestClient";


@NgModule({
	imports: [
		ApiModule,
	],
})
export class KafkaUtilsCommunicationModule {

	static forRoot(): ModuleWithProviders<KafkaUtilsCommunicationModule> {
		return {
			ngModule: KafkaUtilsCommunicationModule,
			providers: [
				{ provide: Configuration, useClass: KafkaUtilsConfigurationService },
			],
		};
	}
}
