package br.com.kafkautils.security.mock

import br.com.kafkautils.security.user.model.Role
import br.com.kafkautils.security.user.model.User
import br.com.kafkautils.security.user.repository.UserRepository
import groovy.transform.CompileStatic
import io.micronaut.security.authentication.UserDetails
import io.micronaut.security.token.generator.TokenGenerator

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Singleton

@CompileStatic
@Singleton
class MockAccessTokenProvider {

	private static final Integer EXPIRATION = 3600

	@Inject
	TokenGenerator tokenGenerator

	@Inject
	UserRepository userRepository

	private User viewer
	private User editor

	String getAdminAccessToken() {
		return tokenGenerator.generateToken(userAdminDetails, EXPIRATION).orElse('')
	}

	String getEditorAccessToken() {
		return tokenGenerator.generateToken(userEditorDetails, EXPIRATION).orElse('')
	}

	String getViewerAccessToken() {
		return tokenGenerator.generateToken(userViewerDetails, EXPIRATION).orElse('')
	}

	String getAccessToken(User user) {
		UserDetails userDetails = new UserDetails(
				user.username,
				[user.role.name()],
				[id: (Object) user.id]
		)
		return tokenGenerator.generateToken(userDetails, EXPIRATION).orElse('')
	}

	@PostConstruct
	void init() {
		String editorName = 'editor'
		editor = userRepository.save(new User(
				null,
				null,
				null,
				editorName,
				editorName,
				'Editor',
				Role.EDITOR,
				true
		)).block()
		String viewerName = 'viewer'
		viewer = userRepository.save(new User(
				null,
				null,
				null,
				viewerName,
				viewerName,
				'Viewer',
				Role.EDITOR,
				true
		)).block()
	}

	private UserDetails getUserEditorDetails() {
		return new UserDetails(
				editor.username,
				[editor.role.name()],
				[id: (Object) editor.id]
		)
	}

	private UserDetails getUserViewerDetails() {
		return new UserDetails(
				viewer.username,
				[viewer.role.name()],
				[id: (Object) viewer.id]
		)
	}

	private UserDetails getUserAdminDetails() {
		return new UserDetails(
				'admin',
				[Role.ADMIN.name()],
				[id: (Object) 1]
		)
	}
}
