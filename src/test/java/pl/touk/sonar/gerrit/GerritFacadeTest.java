package pl.touk.sonar.gerrit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.touk.sonar.GerritPluginException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GerritFacadeTest {
    private static final String SCHEME = "https";
    private static final String HOST = "localhost";
    private static final int PORT = 443;
    private static final String USERNAME = "sonar";
    private static final String PASSWORD = "password";
    private static final String BASE_URL = "";
    private static final String CHANGE_ID = "myProject~master~I8473b95934b5732ac55d26311a706c9c2bde9940";
    private static final String REVISION_ID = "674ac754f91e64a0efb8087e59a176484bd534d1";
    private static final String LIST_FILES_RESPONSE = ")]}'\n" +
        "  {\n" +
        "    \"/COMMIT_MSG\": {\n" +
        "      \"status\": \"A\",\n" +
        "      \"lines_inserted\": 7\n" +
        "    },\n" +
        "    \"gerrit-server/src/main/java/com/google/gerrit/server/project/RefControl.java\": {\n" +
        "      \"lines_inserted\": 5,\n" +
        "      \"lines_deleted\": 3\n" +
        "    },\n" +
        "    \"gerrit-server/src/test/java/com/google/gerrit/server/project/RefControlTest.java\": {\n" +
        "      \"lines_inserted\": 5,\n" +
        "      \"lines_deleted\": 3\n" +
        "    }\n" +
        "  }";
    @Mock private GerritConnector gerritConnectorMock;
    private GerritFacade gerritFacade;

    @Before
    public void setUp() {
        gerritFacade = new GerritFacade(SCHEME, HOST, PORT, USERNAME, PASSWORD, BASE_URL);
        gerritFacade.setGerritConnector(gerritConnectorMock);
    }

    @Test
    public void trimShouldTrimResponseFromPrefix() {
        //given
        String response = ")]}'{}";
        //expect
        assertThat(gerritFacade.trimResponse(response)).isEqualTo("{}");
    }

    @Test
    public void shouldParseListFiles() throws IOException, URISyntaxException, GerritPluginException {
        //given
        when(gerritConnectorMock.listFiles(CHANGE_ID, REVISION_ID)).thenReturn(LIST_FILES_RESPONSE);
        //when
        Map<String, String> files = gerritFacade.listFiles(CHANGE_ID, REVISION_ID);
        //then
        assertThat(files).hasSize(2);
        assertThat(files.get("com.google.gerrit.server.project.RefControl")).isEqualTo("gerrit-server/src/main/java/com/google/gerrit/server/project/RefControl.java");
        assertThat(files.get("com.google.gerrit.server.project.RefControlTest")).isEqualTo("gerrit-server/src/test/java/com/google/gerrit/server/project/RefControlTest.java");
    }
}
