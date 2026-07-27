package org.atdev.artrip.controller;

import org.atdev.artrip.service.ExhibitSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminExhibitSyncControllerTest {

    private static final Long ADMIN_ID = 42L;

    @Mock
    ExhibitSyncService exhibitSyncService;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AdminExhibitSyncController controller = new AdminExhibitSyncController(exhibitSyncService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(fixedLoginUserResolver(ADMIN_ID))
                .build();
    }

    private HandlerMethodArgumentResolver fixedLoginUserResolver(Long adminId) {
        return new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.getParameterType().equals(Long.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                           NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return adminId;
            }
        };
    }

    @Test
    @DisplayName("POST /admin/exhibit-sync 요청 시 로그인한 관리자 id로 동기화를 트리거하고 200을 반환한다")
    void trigger_callsServiceWithLoginAdminId_andReturnsOk() throws Exception {
        mockMvc.perform(post("/admin/exhibit-sync"))
                .andExpect(status().isOk());

        verify(exhibitSyncService).triggerSync(ADMIN_ID);
        verifyNoMoreInteractions(exhibitSyncService);
    }
}